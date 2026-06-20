package com.example.expensetracker.service;

import com.example.expensetracker.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final ObjectMapper objectMapper;
    private final Path usersFilePath;

    public UserService(ObjectMapper objectMapper, @Value("${app.storage.users-file}") String usersFile) {
        this.objectMapper = objectMapper;
        this.usersFilePath = Path.of(usersFile);
    }

    @PostConstruct
    public void createFileIfMissing() throws IOException {
        if (Files.notExists(usersFilePath.getParent())) {
            Files.createDirectories(usersFilePath.getParent());
        }
        if (Files.notExists(usersFilePath)) {
            List<User> defaultUsers = List.of(new User(1L, "student", "student123", "BSc Student"));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(usersFilePath.toFile(), defaultUsers);
        }
    }

    public boolean isValidUser(String username, String password) {
        return findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    public Optional<User> findByUsername(String username) {
        return readUsers().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    private List<User> readUsers() {
        try {
            if (Files.notExists(usersFilePath) || Files.size(usersFilePath) == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(usersFilePath.toFile(), new TypeReference<List<User>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read users.json", e);
        }
    }
}
