package Utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Methods {
    private final Utils_main utils = new Utils_main();
    private final Paths path = new Paths();
    // Начать работу ФС
    public void start_system() throws IOException {
        // Восстановление системный папо, файлов и процессов, если не обходимо
        recover_dir();
        recover_logs();
        recover_files();
        recover_information();
        recover_process();


        // Работа файловой системы
        int command;
        boolean cont = true;
        do {
            System.out.println("\nTask:\n" +
                    "1) Создать файл.\n" +
                    "2) Изменить файл.\n" +
                    "3) Удалить файл.\n" +
                    "0) Выход.\n");
            System.out.print("> ");
            command = utils.inputs();
            switch (command) {
                case 1 -> {
                    System.out.println("Создание файла ->");
                    create();
                    copy_folder(path.files_dir, path.filebackup_dir);
                    recover_files();
                }
                case 2 -> {
                    System.out.println("Изменение файла ->");
                    change();
                    copy_folder(path.files_dir, path.filebackup_dir);
                    recover_files();
                }
                case 3 -> {
                    System.out.println("Удаление файла ->");
                    delete();
                    copy_folder(path.files_dir, path.filebackup_dir);
                    recover_files();
                }
                case 0 -> {
                    cont = false;
                    System.out.println("Выход!");
                    copy_folder(path.files_dir, path.filebackup_dir);
                    recover_files();
                }
                default -> {
                    System.out.println("Неправильная команда. Повторите поптыку !->");
                    copy_folder(path.files_dir, path.filebackup_dir);
                    recover_files();
                }
            }

        } while (cont);
    }

    public void copy_folder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String[] files = src.list();

            assert files != null;
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);

                copy_folder(srcFile, destFile);
            }

        } else {

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);

            in.close();
            out.close();
        }
    }

    public void create() {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите имя файла (.txt / .doc / .docx):\n");
        System.out.print("> ");
        String name = in.nextLine();
        System.out.println("Закончить удаление со сбоем? y\\n\n");
        System.out.print("> ");
        String answer;
        while (true) {
            answer = in.nextLine();
            if (answer.equals("yes") || answer.equals("Yes") || answer.equals("да") || answer.equals("Да") || answer.equals("Д") || answer.equals("Y") || answer.equals("д") || answer.equals("y")) {
                logging(path.date.toString() + " Программа закончена со сбоем.");
                logging(path.date.toString() + " Точка восстановление процесса создания файла.");

                System.exit(0);
            } else if (answer.equals("no") || answer.equals("No") || answer.equals("нет") || answer.equals("Нет") || answer.equals("н") || answer.equals("Н") || answer.equals("N") || answer.equals("n")) {
                logging(path.date.toString() + " Выполняется процесс создания файла.");

                File file = new File(path.file_path, name);

                Date date = new Date();
                if (name.endsWith(".txt") || name.endsWith(".doc") || name.endsWith(".docx")) {
                    logging(date.toString() + " Файл " + name + " корректен");
                    System.out.println("Файл корректен");
                    try {
                        file.createNewFile();
                        File fileCopy = new File(path.backup_path + "files_copy", name);
                        fileCopy.createNewFile();
                    } catch (IOException ignored) {
                    }
                    if (file.exists()) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) {
                        }
                        logging(date.toString() + " Файл " + file + " создан");
                        System.out.println("Файл создан!");
                        logging(date.toString() + " Процесс создания файла окончен.");

                    } else {
                        System.out.println("Файл не создан!");

                        logging(date.toString() + " Файл " + file + " не создан");
                    }
                } else {
                    logging(date.toString() + " Файл " + file + " некорректен");
                    System.out.println("Файл некорректен");
                }
                break;
            }
        }
    }

    public void copy_catalog(boolean type) {
        File reserve_catalog = new File(path.backup_path);
        File copy_catalog = new File(path.backup_path + "/files_copy");
        File copy_journal = new File(path.backup_path + "/backup.txt");
        if (!reserve_catalog.exists()) {
            reserve_catalog.mkdirs();
            copy_catalog.mkdirs();
        }
        try {
            List<String> lines = new ArrayList<>();
            Files.lines(java.nio.file.Paths.get("journal.txt"), StandardCharsets.UTF_8).forEach(lines::add);
            Files.write(copy_journal.toPath(), lines);
            if (type) {
                Path source_path = java.nio.file.Paths.get(path.file_path);
                Path destination_path = java.nio.file.Paths.get(path.backup_file + "/files_copy");
                Files.walk(source_path).filter(Files::isRegularFile).forEach(source -> files_Copy(source, destination_path.resolve(source_path.relativize(source)), true));
            }
        } catch (IOException ignored) {
        }
    }

    public void files_Copy(Path source, Path destination, boolean marker) {
        if (marker) try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
        }
        else if (!marker) try {
            Files.copy(source, destination);
        } catch (IOException ignored) {
        }
    }

    public void change() throws IOException {
        if (!utils.isdir_empty(path.files_dir)) {
            Scanner in = new Scanner(System.in);
            int command;
            boolean con = true;

            System.out.println("Выберите файл для изменения (file.type):\n");
            System.out.print("> ");
            utils.output();
            String name = in.nextLine();

            while (true) {
                File test = new File(path.file_path + "/" + name);

                if (test.exists()) {
                    break;
                } else {
                    System.out.println("Убедитесь в правильности набранного имени файла");
                    System.out.print("> ");
                    name = in.nextLine();
                    logging(path.date.toString() + " Ошибка при вводе имени файла");
                }
            }
            String file = path.file_path + "/" + name;
            logging(path.date.toString() + " Выполняется процесс редактирования файла " + name + ".");
            do {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                System.out.println("\nTask: \n" +
                        "1. Добавить запись. \n" +
                        "2. Прочитать содержимое. \n" +
                        "0. Выйти из процесса редактирования.\n");
                System.out.print("> ");
                command = in.nextInt();
                switch (command) {
                    case 1 -> add(name);
                    case 2 -> read_file(file);
                    case 0 -> {
                        con = false;
                        System.out.println("Файл отредактирован!");
                    }
                    default -> System.out.println("Введена неверная команда, введите команду заново");
                }
            } while (con);
            logging(path.date.toString() + " Процесс редактирования файла " + name + " окончен.");
        } else {
            System.out.println("Каталог файлов пуст. Чтобы начать редактировать файлы, сначала их нужно создать.");
            logging(path.date.toString() + " Попытка начать редактирова файлы, когда их нет");
        }
    }

    public void change(String name, boolean method ) throws IOException {
        if(method) {
            Scanner in = new Scanner(System.in);
            int command;
            boolean con = true;
            String paths = path.file_path + "/" + name;
            System.out.println("Возобновлен процесс редактирования файла " + name + ".");
            logging(path.date.toString() + " Возобновлен процесс редактирования файла " + name + ".");
            do {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                System.out.println("Выберите действие: \n" +
                        "1. Добавить запись. \n" +
                        "2. Прочитать содержимое. \n" +
                        "0. Выйти из процесса редактирования.\n");
                System.out.print("> ");
                command = in.nextInt();
                switch (command) {
                    case 1 -> add(name);
                    case 2 -> read_file(paths);
                    case 0 -> {
                        con = false;
                        System.out.println("Файл отредактирован!");
                    }
                    default -> System.out.println("Введена неверная команда, введите команду заново");
                }
            } while (con);
            logging(path.date.toString() + " Процесс редактирования файла " + name + " окончен.");
        } else {
            System.out.println("Возобновлен процесс создания файла");
            logging(path.date.toString() + " Возобновлен процесс создания файла.");
            Scanner in = new Scanner(System.in);
            System.out.println("Введите имя файла с расширением:\n");
            System.out.print("> ");
            String line = in.nextLine();
            File file = new File(path.file_path, line);
            File backup = new File(path.filebackup_path,line);
            Date date = new Date();
            if (line.endsWith(".txt") || line.endsWith(".doc") || line.endsWith(".docx")) {
                logging(date.toString() + " Файл " + line + " корректен");
                System.out.println("Файл корректен");
                try {
                    file.createNewFile();
                    backup.createNewFile();
                } catch (IOException ignored) {
                }
                if (file.exists() && backup.exists()) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                    logging(date.toString() + " Файл " + file + " создан");
                    System.out.println("Файл создан!");
                    logging(date.toString() + " Процесс создания файла окончен.");

                } else {
                    System.out.println("Файл не создан!");

                    logging(date.toString() + " Файл " + file + " не создан");
                }
            } else {
                logging(date.toString() + " Файл " + file + " некорректен");
                System.out.println("Файл некорректен");
            }
        }
    }

    public void delete() {
        Scanner in = new Scanner(System.in);
        logging(path.date.toString() + " Начат процесс удаления файла.");
        utils.output();
        String name = in.nextLine();
        File file = new File(path.file_path, name);
        File file_copy = new File(path.filebackup_dir, name);
        System.out.println("Закончить удаление со сбоем? y\\n\n");
        System.out.print("> ");
        String answer;
        boolean flag = true;
        while (flag) {
            answer = in.nextLine();
            if (answer.equals("yes") || answer.equals("Yes") || answer.equals("да") || answer.equals("Да") || answer.equals("Д") || answer.equals("Y") || answer.equals("д") || answer.equals("y")) {
                logging(path.date.toString() + " Программа закончена со сбоем.");

                System.exit(0);
            } else if (answer.equals("no") || answer.equals("No") || answer.equals("нет") || answer.equals("Нет") || answer.equals("н") || answer.equals("Н") || answer.equals("N") || answer.equals("n")) {
                if (file.delete() && file_copy.delete()) {
                    System.out.println("Файл удален!");
                    logging(path.date.toString() + " Файл " + file + " удален");
                } else {
                    System.out.println("Удаление прервано!");
                    logging(path.date.toString() + " Файл " + file + " не удален");
                }
                flag = false;
            } else {
                System.out.println("Некорректный ввод! Попробуйте еще раз!");
                in.next();
            }
        }
        logging(path.date.toString() + " Процесс удаления файла окончен.");
    }

    public void read_file(String name) throws IOException {

        File file = new File(name);
        FileReader fr = new FileReader(name);
        Scanner scan = new Scanner(fr);
        if (file.length() == 0)
            System.out.println("Файл пуст!");
        else {
            while (scan.hasNextLine()) {
                System.out.println(scan.nextLine());
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        fr.close();
    }

    public void add(String name) {
        Scanner in = new Scanner(System.in);
        File file = new File(path.file_path + "/" + name);
        File backup = new File(path.filebackup_path + "/" + name);


        System.out.println("Введите строку, которую хотите добавить:\n");
        System.out.print("> ");
        String str = in.nextLine();
        str = str + "\n";
        System.out.println("Закончить добавление со сбоем? y\\n\n");
        System.out.print("> ");
        String answer = in.nextLine();
        if (file.exists()) {
            if (answer.equals("yes") || answer.equals("Yes") || answer.equals("да") || answer.equals("Да") || answer.equals("Д") || answer.equals("Y") || answer.equals("д") || answer.equals("y")) {
                logging(path.date.toString() + " Программа закончена со сбоем.");
                logging(path.date.toString() + " Точка восстановления процесса изменения файла " + name + ".");
                System.exit(0);
            } else if (answer.equals("no") || answer.equals("No") || answer.equals("нет") || answer.equals("Нет") || answer.equals("н") || answer.equals("Н") || answer.equals("N") || answer.equals("n")) {
                try {
                    FileWriter backup_writer = new FileWriter(backup, true);
                    FileWriter writer = new FileWriter(file, true);

                    backup_writer.append(str);
                    backup_writer.flush();
                    backup_writer.close();
                    writer.append(str);
                    writer.flush();
                    writer.close();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                    System.out.println("Строка добавлена");
                    Date date = new Date();
                    logging(date.toString() + " Добавление строки в файл " + file.getName());
                } catch (IOException ignored) {
                }
            }
        } else {
            System.out.println("Убедитесь в правильности набранного имени файла");
            logging(path.date.toString() + " Ошибка при вводе имени файла");
        }
    }

    public void logging(String str) {
        try {
            FileWriter log = new FileWriter(path.logs_path + "/logs.txt", true);
            FileWriter logTwo = new FileWriter(path.backup_path + "/backup.txt", true);
            log.write(str);
            log.append("\r\n");
            log.flush();
            log.close();

            logTwo.write(str);
            logTwo.append("\r\n");
            logTwo.flush();
            logTwo.close();

        } catch (IOException ignored) {
        }
    }

    // Восстановление системных дирректорий
    public void recover_dir() throws  IOException {
        if(!path.logs.exists()) {
            if(path.logs.mkdir()) {
                if(path.logs_file.createNewFile()) {
                    System.out.println("Файловая система обнаружила отсуствие папки logs для работы. Папка восстановлена.");
                    logging(path.date.toString() + " Файловая система обнаружила отсуствие папки logs для работы. Папка восстановлена.");
                }
            }
            else {
                System.out.println("Произошла ошибка при попытке восстановить файловую систему.");
                logging(path.date.toString() + " Произошла ошибка при попытке восстановить файловую систему.");
            }
        }
        if (!path.logs_file.exists()) {
            if(path.logs_file.createNewFile()) {
                System.out.println("Файл logs.txt был удален. Файл восстановлен.");
                logging(path.date.toString() + " Файл logs.txt был удален. Файл восстановлен.");
            }
            else {
                System.out.println("Ошибка программы. Завершение.");
                System.exit(0);
            }
        }
        if(!path.backup.exists()) {
            if(path.backup.mkdir()) {
                if(path.backup_file.createNewFile() && path.filebackup_dir.mkdir()) {
                    System.out.println("Файловая система обнаружила отсуствие папки backup для работы. Папка восстановлена.");
                    System.out.println("Файловая система обнаружила отсуствие папки files_copy для работы. Папка восстановлена.");
                    logging(path.date.toString() + " Файловая система обнаружила отсуствие папки backup для работы. Папка восстановлена.");
                    logging(path.date.toString() + " Файловая система обнаружила отсуствие папки files_copy для работы. Папка восстановлена.");
                }
            }
            else {
                System.out.println("Произошла ошибка при попытке восстановить файловую систему. Ошибка 50");
                logging(path.date.toString() + " Произошла ошибка при попытке восстановить файловую систему. Ошибка 50");
            }
        }
        if(!path.filebackup_dir.exists()) {
            if(path.filebackup_dir.mkdir()) {
                if(path.backup_file.createNewFile() && path.filebackup_dir.mkdir()) {
                    System.out.println("Файловая система обнаружила отсуствие папки backup для работы. Папка восстановлена.");
                    logging(path.date.toString() + " Файловая система обнаружила отсуствие папки backup для работы. Папка восстановлена.");
                }
            }
            else {
                System.out.println("Произошла ошибка при попытке восстановить файловую систему. Ошибка 50");
                logging(path.date.toString() + " Произошла ошибка при попытке восстановить файловую систему. Ошибка 50");
            }
        }
        if(!path.files_dir.exists()) {
            if(path.files_dir.mkdir()) {
                System.out.println("Файловая система обнаружила отсуствие папки files для работы. Папка восстановлена.");
                logging(path.date.toString() + " Файловая система обнаружила отсуствие папки logs для работы. Папка восстановлена.");
            }
            else {
                System.out.println("Произошла ошибка при попытке восстановить файловую систему.");
                logging(path.date.toString() + " Произошла ошибка при попытке восстановить файловую систему.");
            }
        }
    }

    public void recover_logs() throws IOException {
        List<String> links1 = Files.readAllLines(java.nio.file.Paths.get(path.logs_path + "/logs.txt"), StandardCharsets.UTF_8);
        if (path.logs_file.length() != 0) {
            if (path.logs_file.length() != path.backup_file.length() && path.logs_file.length() != 0) {
                logging(path.date.toString() + " Журнал был изменен вне программы. Восстановление успешно.");
                System.out.println("Изменен журнал! Идет процесс восстановления. Восстановление успешно.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                copy_files(path.backup_file, path.logs_file);
                System.out.println("Журнал восстановлен!");
            } else {
                if (links1.get(links1.size() - 1).endsWith("00")) {
                    logging(path.date.toString() + " Прерван процесс создания файла.");
                } else {
                    if (links1.get(links1.size() - 1).endsWith("10")) {
                        logging(path.date.toString() + " Прерван процесс редактирования файла.");
                    } else if (links1.get(links1.size() - 1).endsWith("20")) {
                        logging(path.date.toString() + " Прерван процесс удаления файла.");
                    } else {
                        copy_files(path.backup_file, path.logs_file);
                    }
                }
            }
        }
    }

    public void recover_files() {
        File catalog = new File(path.file_path);
        boolean flag = false;
        File copy_catalog = new File(path.backup_path + "/files_copy");
        if (!catalog.exists()) {
            catalog.mkdir();
            utils.helper();
            logging(path.date.toString() + "[ВНИМАНИЕ!] Каталог хранения файлов был восстановлен после стороннего удаления.");
            copy_catalog(true);
            System.out.println("[ВНИМАНИЕ!] Каталог хранения файлов был восстановлен после стороннего удаления.");
        } else if (catalog.listFiles().length != copy_catalog.listFiles().length) {
            utils.helper();
            logging(path.date.toString() + "[ВНИМАНИЕ!] Каталог хранения файлов был восстановлен после стороннего добавления или удаления файлов.");
            copy_catalog(true);
            System.out.println("[ВНИМАНИЕ!] Каталог хранения файлов был восстановлен после стороннего добавления или удаления файлов.");
            for (int index = 0; index < copy_catalog.listFiles().length; index++) {
                if (catalog.listFiles()[index].lastModified() > copy_catalog.listFiles()[index].lastModified())
                    flag = true;
            }
            if (flag) {
                utils.helper();
                logging(path.date.toString() + "[ВНИМАНИЕ!] Каталог хранения файлов был восстановлен после стороннего изменения файлов.");
                copy_catalog(true);
                System.out.println("[ВНИМАНИЕ!] Каталог хранения файлов был восстановлен после стороннего изменения файлов.");
            }
        }
    }

    public void recover_information() throws IOException {
        File myFolder = new File(path.file_path);
        File myFolderCopy = new File(path.backup_path + "/files_copy");

        for (File item : myFolder.listFiles()) {

            for (File itemCopy : myFolderCopy.listFiles()) {
                if (item.getName().equals(itemCopy.getName())) {
                    System.out.println(item.getAbsolutePath());
                    if (!utils.compare(item.getAbsolutePath(), itemCopy.getAbsolutePath())) {
                        System.out.println("[ВНИМАНИЕ!] Информация из файла " + item.getName() + " была восстановлена после стороннего редактирования.");
                        logging(path.date.toString() + " Файл " + item.getName() + " был редактирован.");
                        copy_files(itemCopy, item);
                    }
                    break;
                }
            }
        }
    }

    // Восстановление процесса редактирования или создания файлов
    public void recover_process() throws IOException {
        List<String> files = utils.get_files();
        List<String> logs = utils.get_info(path.backup_file);
        if(logs.get(logs.size()-1).contains("Выполняется процесс редактирования файла") ||
                logs.get(logs.size()-1).contains("Точка восстановления процесса изменения файла")) {
            for(String s : files) {
                if(logs.get(logs.size()-1).contains(s)) {
                    change(s, true);
                }
            }
        }
        if(logs.get(logs.size()-1).contains("Выполняется процесс создания файла.") ||
                logs.get(logs.size()-1).contains("Точка восстановление процесса создания файла.")) {
            change("", false);
        }
    }

    public void copy_files(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel(); FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
}
