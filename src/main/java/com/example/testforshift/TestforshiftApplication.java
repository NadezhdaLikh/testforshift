package com.example.testforshift;

import jakarta.annotation.Nonnull;
import org.apache.commons.cli.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

@SpringBootApplication
public class TestforshiftApplication implements CommandLineRunner {

	public static void main(String[] args) {
        SpringApplication.run(TestforshiftApplication.class, args);
	}

    @Override
    public void run(@Nonnull String... args) throws Exception {
        Options options = getOptions();

        CommandLineParser commandLineParser = new DefaultParser();

        try {
            CommandLine commandLine = commandLineParser.parse(options, args);

            Path outputDir;

            if (commandLine.hasOption("o")) {
                outputDir = Path.of(commandLine.getOptionValue("o"));
            } else outputDir = Path.of(System.getProperty("user.dir"));

            if (!(Files.exists(outputDir) & Files.isDirectory(outputDir))) {
                System.err.println("Ошибка! Данный путь " + outputDir + " не существует.");
                System.exit(1);
            }

            Path intsFilePath;
            Path floatsFilePath;
            Path strsFilePath;

            if (commandLine.hasOption("p")) {
                intsFilePath = outputDir.resolve(commandLine.getOptionValue("p") + "integers.txt");
                floatsFilePath = outputDir.resolve(commandLine.getOptionValue("p") + "floats.txt");
                strsFilePath = outputDir.resolve(commandLine.getOptionValue("p") + "strings.txt");
            } else {
                intsFilePath = outputDir.resolve("integers.txt");
                floatsFilePath = outputDir.resolve("floats.txt");
                strsFilePath = outputDir.resolve("strings.txt");
            }

            List<Long> intList = new ArrayList<>();
            List<Double> floatList = new ArrayList<>();
            List<String> strList = new ArrayList<>();

            for (String arg : commandLine.getArgs()) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(arg))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        String trimmedLine = line.trim(); // returns a string whose value is this string, with all leading and trailing space removed

                        if (!trimmedLine.isBlank()) { // returns true if the string is empty or contains only white space codepoints, otherwise false
                            try {
                                intList.add(Long.parseLong(trimmedLine));
                            } catch (NumberFormatException numberFormatException1) {
                                try {
                                    floatList.add(Double.parseDouble(trimmedLine));
                                } catch (NumberFormatException numberFormatException2) {
                                    strList.add(trimmedLine);
                                }
                            }
                        }
                    }
                }
            }

            try {
                writeToFile(intList, intsFilePath, commandLine.hasOption("a"));
                writeToFile(floatList, floatsFilePath, commandLine.hasOption("a"));
                writeToFile(strList, strsFilePath, commandLine.hasOption("a"));
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println(fileNotFoundException.getMessage());
                System.exit(2);
            }

            if (commandLine.hasOption("s")) {
                printStatsForInt(intList, false);
                printStatsForFloat(floatList, false);
                printStatsForStr(strList, false);
            }

            if (commandLine.hasOption("f")) {
                printStatsForInt(intList, true);
                printStatsForFloat(floatList, true);
                printStatsForStr(strList, true);
            }
        } catch (ParseException parseException) {
            System.err.println(parseException.getMessage());
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        Option oOpt = new Option("o", true, "Задать путь для результатов");
        Option pOpt = new Option("p", true, "Задать префикс имен выходных файлов");
        Option aOpt = new Option("a", false, "Задать режим добавления в существующие файлы");
        Option sOpt = new Option("s", false, "Собрать короткую статистику");
        Option fOpt = new Option("f", false, "Собрать полную статистику");

        options.addOption(oOpt);
        options.addOption(pOpt);
        options.addOption(aOpt);
        options.addOption(sOpt);
        options.addOption(fOpt);

        return options;
    }

    private static <T> void writeToFile(List<T> data, Path path, boolean appendMode) throws FileNotFoundException {
        if (!data.isEmpty()) {
            if (appendMode) {
                if (Files.notExists(path)) {
                    throw new FileNotFoundException("Ошибка! Файл " + path + " не существует. Прежде чем использовать опцию -а (режим добавления в существующий файл), создайте соответствующий файл.");
                }
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile(), appendMode))) {
                for (T value : data) {
                    bufferedWriter.write(value + "\n");
                }
            } catch (IOException ioException) {
                System.err.println(ioException.getMessage());
            }
        }
    }

    private static void printStatsForInt(List<Long> intList, boolean isFull) {
        if (!intList.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("0.##"); // # prints a digit if provided, nothing otherwise

            int intCnt = intList.size(); // Количество элементов

            long intSum = intList.stream().mapToLong(Long::longValue).sum(); // Сумма всех найденных целочисленных элементов

            float intAvr = (float) intSum / intCnt; // Среднее значение ряда целочисленных элементов

            long maxInt = Collections.max(intList);
            long minInt = Collections.min(intList);

            if (!isFull) {
                System.out.printf("Краткая статистика для типа integer: кол-во записанных элементов - %d%n", intCnt);
            } else System.out.printf("Полная статистика для типа integer: кол-во записанных элементов - %d; сумма всех элементов - %d; среднее арифметическое (округленное значение) - %s; min элемент - %d; max элемент - %d.\n",
                    intCnt,
                    intSum,
                    decimalFormat.format(intAvr),
                    minInt,
                    maxInt);

        } else System.out.println("Не удалось собрать статистику по типу integer: элементы данного типа в исходных файлах отсутствуют.");
    }

    private static void printStatsForFloat(List<Double> floatList, boolean isFull) {
        if (!floatList.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00##"); // # prints a digit if provided, nothing otherwise

            int floatCnt = floatList.size();

            double floatSum = floatList.stream().mapToDouble(Double::doubleValue).sum(); // Сумма всех элементов с плавающей запятой

            double floatAvr = floatSum / floatCnt; // Среднее значение ряда элементов с плавающей запятой

            double maxFloat = Collections.max(floatList);
            double minFloat = Collections.min(floatList);

            if (!isFull) {
                System.out.printf("Краткая статистика для типа float: кол-во записанных элементов - %d%n", floatCnt);
            } else System.out.printf("Полная статистика для типа float: кол-во записанных элементов - %d; сумма всех элементов (округленное значение) - %s; среднее арифметическое (округленное значение) - %s; min элемент - %s; max элемент - %s.\n",
                    floatCnt,
                    decimalFormat.format(floatSum),
                    decimalFormat.format(floatAvr),
                    minFloat,
                    maxFloat);

        } else System.out.println("Не удалось собрать статистику по типу float: элементы данного типа в исходных файлах отсутствуют.");
    }

    private static void printStatsForStr(List<String> strList, boolean isFull) {
        if (!strList.isEmpty()) {
            int strCnt = strList.size();

            strList.sort(Comparator.comparingInt(String::length));
            String shortestStr = strList.getFirst();
            String longestStr = strList.getLast();

            if (!isFull) {
                System.out.printf("Краткая статистика для типа string: кол-во записанных элементов - %d%n", strCnt);
            } else System.out.printf("Полная статистика для типа string: кол-во записанных элементов - %d; самая короткая строка - %s; самая длинная строка - %s.", strCnt, shortestStr, longestStr);

        } else System.out.println("Не удалось собрать статистику по типу string: элементы данного типа в исходных файлах отсутствуют.");
    }
}
