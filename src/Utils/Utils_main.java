package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Utils_main {
    private final Paths path = new Paths();

    // Проверка на правильность ввода
    public int inputs() {
        Scanner in = new Scanner(System.in);
        while (!in.hasNextInt()) {
            System.out.println("Некорректный ввод!\nПопробуйте еще раз!\n");
            System.out.print("> ");
            in.next();
        }
        return in.nextInt();
    }

    // Вспомогательная функция вывода имен файлов
    public void output() {
        System.out.println("Select file name (file.type):\n");
        File folder = new File(path.file_path);
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles) {
            System.out.println(listOfFile.getName());
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
    }

    // Вспомогательная функция копирования файлов
    public void files_copy(Path source, Path destination, boolean marker) {
        if (marker) try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
        else if (!marker) try {
            Files.copy(source, destination);
        } catch (IOException ignored) {
        }
    }

    public void remove_Files() {
        for (File file : new File(path.file_path).listFiles()) {
            file.delete();
        }
    }

    // Вспомогательная функция копирования файлов из одной директории в другую
    public void helper() {
        try {
            remove_Files();
            Path source_path = java.nio.file.Paths.get(path.backup_path + "/files_copy");
            Path destination_path = java.nio.file.Paths.get(path.file_path);
            Files.walk(source_path).forEach(source -> files_copy(source, destination_path.resolve(source_path.relativize(source)), false));
        } catch (IOException ignored) {
        }
    }

    // Вспомогательная функция, которая дает список всех файлов
    public List<String> get_files() {
        List<String> files = new ArrayList<>();
        for(File file : path.files_dir.listFiles()) {
            files.add(file.getName());
        }
        return files;
    }

    // Вспомогательная функция, которая сообщает, пустой ли каталог
    public boolean isdir_empty(File file) {
        return (file.isDirectory()) && (file.list().length == 0);
    }

    // Вспомогательная функция, которая возвращает информацию из файла
    public List<String> get_info(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> result = new ArrayList<>();
        String s = bufferedReader.readLine();
        while (s != null) {
            result.add(s);
            s = bufferedReader.readLine();
        }
        return result;
    }

    // Вспомогательная функция, которая позволяет узнать, были ли редактированы логи вне программы
    public boolean compare(String source, String dest) throws IOException {
        Scanner sc = new Scanner(new File(source));
        Scanner scOne = new Scanner(new File(dest));
        String s = "";
        String sOne = "";
        String[] splitted;
        String[] splittedOne;
        while (sc.hasNext()) {
            splitted = sc.nextLine().split("\n");
            s += splitted[0];
        }
        while (scOne.hasNext()) {
            splittedOne = scOne.nextLine().split("\n");
            sOne += splittedOne[0];
        }
        return s.equals(sOne);
    }
}
