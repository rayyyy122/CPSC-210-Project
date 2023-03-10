package persistence;

import model.Account;
import model.Course;
import model.Semester;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads account from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Account read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseAccount(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses account from JSON object and returns it
    public Account parseAccount(JSONObject jsonObject) {
        Account a = new Account();
        addSemesters(a, jsonObject);
        return a;
    }

    // MODIFIES: a
    // EFFECTS: parses semesters from JSON object and adds them to Account
    private void addSemesters(Account a, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("semesters"); 
        for (Object json : jsonArray) {
            JSONObject nextSemester = (JSONObject) json;
            addSemester(a, nextSemester);
        }
    }

    // MODIFIES: a
    // EFFECTS: parses semester from JSON object and adds it to Account
    private void addSemester(Account a, JSONObject jsonObject) {
        String des = jsonObject.getString("Description");
        Semester s = new Semester(des);
        JSONArray grades = jsonObject.getJSONArray("courses");
        for (Object json : grades) {
            JSONObject nextCourse = (JSONObject) json;
            addCourse(s, nextCourse);
        }
        a.addSemesterDirect(s);

//        String name = jsonObject.getString();
//        Semester semester = new Semester(name);
//        a.addSemester(semester.getSemesterName());
    }

    // MODIFIES: a
    // EFFECTS: parse course from JSON object and adds it to Semester
    private void addCourse(Semester s, JSONObject jsonObject) {
        String courseName = jsonObject.getString("name");
        double grade = jsonObject.getDouble("grade");
        Course course = new Course(courseName, grade);
        s.addCourse(course);
    }
}

