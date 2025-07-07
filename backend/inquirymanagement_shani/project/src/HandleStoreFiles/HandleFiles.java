package HandleStoreFiles;

import Data.Inquiry;
import Data.Representative;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

public class HandleFiles {

    public static void saveFile(IForSaving forSaving) throws IOException {
        File dir = new File(forSaving.getFolderName());
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, forSaving.getFileName() + ".txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file))) {
            outputStreamWriter.write(forSaving.getData());
        }
    }


    public static void deleteFile(IForSaving forSaving) throws IOException {
        File file = new File(forSaving.getFolderName(), forSaving.getFileName()+".txt");
        if (file.exists()) {
            file.delete();
        }
    }

    public static void updateFile(IForSaving forSaving) throws IOException {
        saveFile(forSaving);
    }

    public String getFileName(IForSaving forSaving) {
        return forSaving.getFileName();
    }

    public void saveFiles(List<IForSaving> forSavingList) throws IOException {
        for (IForSaving file:forSavingList)
            saveFile(file);
    }


    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz==String.class||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class;
    }

    public String getCSVDataRecursive(Object obj) {
        if (obj == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null && !isPrimitiveOrWrapper(value.getClass())) {
                    result.append(getCSVDataRecursive(value)).append(", ");
                } else {
                    result.append(value).append(", ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }
        return result.toString();
    }

    public boolean saveCSV(Object obj, String filename) {
        if (obj == null || filename == null || filename.isEmpty()) {
            return false;
        }

        String csvData = getCSVDataRecursive(obj);
        String className = obj.getClass().getName();
        String fullCsvData = className + ", " + csvData;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(fullCsvData);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object readCsv(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            if (line != null) {
                String[] values = line.split(", ");
                Class<?> clazz = Class.forName(values[0]);
                Object obj = clazz.getDeclaredConstructor().newInstance();

                Field[] fields = clazz.getDeclaredFields();
                Arrays.sort(fields, Comparator.comparing(Field::getName));

                for (int i = 0; i < fields.length-1; i++) {
                    fields[i].setAccessible(true);
                    if (isPrimitiveOrWrapper(fields[i].getType())) {
                        if (isPrimitiveOrWrapper(fields[i].getType())) {
                            if (fields[i].getType() == int.class) {
                                fields[i].set(obj, Integer.parseInt(values[i + 1]));
                            } else {
                                fields[i].set(obj, values[i + 1]);
                            }
                        }
                    } else {
                        Object innerObj = readCsv(fields[i].getType().getName());
                        fields[i].set(obj, innerObj);
                    }
                }
                return obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        String strVal = value.toString();

        if (targetType == int.class || targetType == Integer.class)
            return Integer.parseInt(strVal);
        if (targetType == double.class || targetType == Double.class)
            return Double.parseDouble(strVal);
        if (targetType == boolean.class || targetType == Boolean.class)
            return Boolean.parseBoolean(strVal);
        if (targetType == long.class || targetType == Long.class)
            return Long.parseLong(strVal);
        if (targetType == float.class || targetType == Float.class)
            return Float.parseFloat(strVal);
        if (targetType == short.class || targetType == Short.class)
            return Short.parseShort(strVal);
        if (targetType == byte.class || targetType == Byte.class)
            return Byte.parseByte(strVal);
        if (targetType == char.class || targetType == Character.class)
            return strVal.charAt(0);
        if (targetType == String.class)
            return strVal;

        return value;
    }
    public Object readTxt(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String classNameLine = reader.readLine();
            if (classNameLine == null || !classNameLine.startsWith("className: ")) {
                throw new IllegalArgumentException("File is missing className line: " + file.getName());
            }

            String className = classNameLine.substring("className: ".length()).trim();
            if (className.startsWith("Data.")) {
                className = className.substring("Data.".length());
            }


            if (!className.matches("[A-Z][A-Za-z0-9_]*")) {
                throw new ClassNotFoundException("Invalid class name: " + className);
            }


            Class<?> clazz = Class.forName("Data." + className);
            Inquiry inquiry = (Inquiry) clazz.getDeclaredConstructor().newInstance();


            inquiry.setClassName(className);


            String line;
            Map<String, String> dataMap = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    dataMap.put(parts[0].trim(), parts[1].trim());
                } else {
                    dataMap.put(parts[0].trim(), "");
                }
            }


            for (Field field : getAllFields(clazz)) {
                field.setAccessible(true);
                String value = dataMap.get(field.getName());
                if (value == null || value.equalsIgnoreCase("null") || value.isEmpty()) {
                    continue;
                }

                Class<?> fieldType = field.getType();

                if (List.class.isAssignableFrom(fieldType)) {

                    String[] items = value.replaceAll("[\\[\\] ]", "").split(",");
                    List<String> list = new ArrayList<>();
                    for (String item : items) {
                        if (!item.isEmpty()) list.add(item);
                    }
                    field.set(inquiry, list);

                } else if (fieldType == int.class || fieldType == Integer.class) {
                    field.set(inquiry, Integer.parseInt(value));

                } else if (fieldType == LocalDateTime.class) {
                    field.set(inquiry, LocalDateTime.parse(value));

                } else if (fieldType.isEnum()) {
                    Object enumVal = Enum.valueOf((Class<Enum>) fieldType, value);
                    field.set(inquiry, enumVal);

                } else {
                    field.set(inquiry, value);
                }
            }


            String repIdStr = dataMap.get("representativeId");
            String repName = dataMap.get("representativeName");
            if (repIdStr != null && !repIdStr.isEmpty() && !repIdStr.equalsIgnoreCase("null")) {
                try {
                    int repId = Integer.parseInt(repIdStr);
                    Representative rep = new Representative();
                    rep.setId(repId);
                    rep.setName(repName != null ? repName : "");
                    inquiry.setRepresentative(rep);
                } catch (NumberFormatException e) {
                    System.err.println("invalid representativeId in file " + file.getName());
                }
            }

            return inquiry;

        } catch (Exception e) {
            System.err.println("error reading file: " + file.getName());
            e.printStackTrace();
            return null;
        }
    }

    public List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }


    public void saveFile(Inquiry forSaving, String customFolderName) {
        try {
            String folderName = (customFolderName != null && !customFolderName.isBlank())
                    ? customFolderName
                    : forSaving.getFolderName();

            File dir = new File(folderName);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("failed to create directory: " + dir.getAbsolutePath());
            }

            File file = new File(dir, forSaving.getCode() + ".txt");


            if (file.exists()) {
                if (!file.delete()) {
                    throw new IOException("failed to delete existing file: " + file.getAbsolutePath());
                }
            }


            try (FileWriter writer = new FileWriter(file)) {
                writer.write(forSaving.getData());
            }

        } catch (Exception e) {
            System.err.println("error saving inquiry: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveFile(Inquiry forSaving) {
        saveFile(forSaving, null);
    }


}



