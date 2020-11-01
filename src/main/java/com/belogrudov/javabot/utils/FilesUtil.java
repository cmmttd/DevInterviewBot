package com.belogrudov.javabot.utils;

import com.belogrudov.javabot.data.Question;
import com.belogrudov.javabot.data.QuestionsTable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FilesUtil {

    @Autowired
    QuestionsTable questionsTable;

    @SneakyThrows
    public void fillingDB() {
        //result questions pool
        List<Question> questions = new ArrayList<>();

        HashMap<Path, String> contentMap = new HashMap<>();
        String dir = "src/main/resources/content/";
        contentMap.put(Paths.get(dir + "patterns.md"), "\\[к оглавлению]\\(#Шаблоны-проектирования\\)");
        contentMap.put(Paths.get(dir + "jcf.md"), "\\[к оглавлению]\\(#java-collections-framework\\)");
        contentMap.put(Paths.get(dir + "io.md"), "\\[к оглавлению]\\(#Потоки-вводавывода-в-java\\)");
        contentMap.put(Paths.get(dir + "concurrency.md"), "\\[к оглавлению]\\(#Многопоточность\\)");
        contentMap.put(Paths.get(dir + "serialization.md"), "\\[к оглавлению]\\(#Сериализация\\)");
        contentMap.put(Paths.get(dir + "servlets.md"), "\\[к оглавлению]\\(#servlets-jsp-jstl\\)");
        contentMap.put(Paths.get(dir + "log.md"), "\\[к оглавлению]\\(#Журналирование\\)");
        contentMap.put(Paths.get(dir + "test.md"), "\\[к оглавлению]\\(#Тестирование\\)");
        contentMap.put(Paths.get(dir + "db.md"), "\\[к оглавлению]\\(#Базы-данных\\)");
        contentMap.put(Paths.get(dir + "core.md"), "\\[к оглавлению]\\(#java-core\\)");
        contentMap.put(Paths.get(dir + "web.md"), "\\[к оглавлению]\\(#Основы-web\\)");
        contentMap.put(Paths.get(dir + "java8.md"), "\\[к оглавлению]\\(#java-8\\)");
        contentMap.put(Paths.get(dir + "jdbc.md"), "\\[к оглавлению]\\(#jdbc\\)");
        contentMap.put(Paths.get(dir + "oop.md"), "\\[к оглавлению]\\(#ООП\\)");
        contentMap.put(Paths.get(dir + "jvm.md"), "\\[к оглавлению]\\(#jvm\\)");
        contentMap.put(Paths.get(dir + "xml.md"), "\\[к оглавлению]\\(#xml\\)");

        for (Map.Entry<Path, String> file : contentMap.entrySet()) {
            String fileContent = String.join("\n", Files.readAllLines(file.getKey()));
            String[] split = fileContent.split(file.getValue());

            for (String ss : split) {
                StringBuilder key = new StringBuilder(),
                        val = new StringBuilder();
                for (String s : ss.split("\n")) {
                    if (s.startsWith("##")) {
                        key.append(s).append("\n");
                    } else {
                        val.append(s).append("\n");
                    }
                }
                questions.add(new Question(key.toString(), val.toString()));
            }
        }
        questionsTable.saveAll(questions);
        questionsTable.flush();
    }
}
