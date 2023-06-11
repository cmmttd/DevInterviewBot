package com.belogrudov.javabot.utils;

import com.belogrudov.javabot.controller.MessageDispatcher;
import com.belogrudov.javabot.data.Question;
import com.belogrudov.javabot.data.QuestionsRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilesUtil {

    @Value("${telegram.content.path}")
    private String contentPath;

    private final QuestionsRepo questionsRepo;

    @Transactional
    public void fillingDB() {
        List<Question> questions = new ArrayList<>();
        List<Path> contentFiles = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(Paths.get(contentPath), 1)) {
            contentFiles = stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().endsWith("md"))
                    .filter(file -> !file.toString().contains("README"))
                    .toList();
        } catch (IOException e) {
            log.error("Can't collect content files list", e);
        }
        questionsRepo.deleteAll();
        for (Path file : contentFiles) {
            Pair pair = new Pair(new StringBuilder(), new StringBuilder());
            String[] lines = new String[0];
            try {
                lines = Files.readAllLines(file).toArray(String[]::new);
            } catch (IOException e) {
                log.error("File not reachable: {}", file, e);
            }

            for (String line : lines) {
                if (line.isEmpty() || line.contains("[к оглавлению]")) {
                    continue;
                }
                if (line.contains("Источники")) {
                    break;
                }
                if (line.startsWith("##")) {
                    if (pair.getDescription().isEmpty()) {
                        pair.getQuestion().append("\n");
                        pair.getQuestion().append(line);
                    } else {
                        questionsRepo.save(new Question(pair.getQuestion().toString().strip(), pair.getDescription().toString().strip()));
                        pair = new Pair(new StringBuilder(), new StringBuilder());
                        pair.getQuestion().append(line);
                    }
                } else {
                    if (!pair.getQuestion().isEmpty()) {
                        pair.getDescription().append(line);
                        pair.getDescription().append("\n");
                    }
                }
            }
//            questionsRepo.saveAll(questions);
//            questions.clear();
        }
        questionsRepo.flush();
        MessageDispatcher.maxQNumber = questionsRepo.findAll().size();
    }

    //    @Scheduled
    @SneakyThrows
    public void uploadContent() {
        //tbd
//        Files.deleteIfExists(CONTENT_PATH.toAbsolutePath());
//        Files.createDirectory(CONTENT_PATH.toAbsolutePath());
//        String path = CONTENT_PATH + "/temp.zip";
//        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
//            fileOutputStream.getChannel()
//                    .transferFrom(
//                            Channels.newChannel(new URL("https://github.com/enhorse/java-interview/archive/refs/heads/master.zip").openStream()),
//                            0, Long.MAX_VALUE);
//        }
//        try (ZipFile zipFile = new ZipFile(path)) {
//            zipFile.extractAll(CONTENT_PATH.toString());
//        } catch (ZipException e) {
//            e.printStackTrace();
//        }
    }

    @Data
    @AllArgsConstructor
    private static class Pair {
        StringBuilder question;
        StringBuilder description;
    }
}
