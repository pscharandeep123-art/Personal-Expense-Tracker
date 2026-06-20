package com.example.expensetracker.service;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ObjectMapper objectMapper;
    private final Path expensesFilePath;

    public ExpenseService(ObjectMapper objectMapper, @Value("${app.storage.expenses-file}") String expensesFile) {
        this.objectMapper = objectMapper;
        this.expensesFilePath = Path.of(expensesFile);
    }

    @PostConstruct
    public void createFileIfMissing() throws IOException {
        if (Files.notExists(expensesFilePath.getParent())) {
            Files.createDirectories(expensesFilePath.getParent());
        }
        if (Files.notExists(expensesFilePath)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(expensesFilePath.toFile(), new ArrayList<Expense>());
        }
    }

    public List<Expense> findByUsername(String username) {
        return readExpenses().stream()
                .filter(expense -> expense.getUsername().equalsIgnoreCase(username))
                .sorted(Comparator.comparing(Expense::getDate, Comparator.reverseOrder())
                        .thenComparing(Expense::getId, Comparator.reverseOrder()))
                .toList();
    }

    public List<Expense> search(String username, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findByUsername(username);
        }
        String searchText = keyword.trim().toLowerCase();
        return findByUsername(username).stream()
                .filter(expense -> contains(expense.getTitle(), searchText)
                        || contains(expense.getDescription(), searchText)
                        || expense.getCategory().getDisplayName().toLowerCase().contains(searchText)
                        || expense.getAmount().toPlainString().contains(searchText)
                        || expense.getDate().toString().contains(searchText))
                .toList();
    }

    public Optional<Expense> findByIdForUser(Long id, String username) {
        return readExpenses().stream()
                .filter(expense -> expense.getId().equals(id) && expense.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public Expense save(Expense expense, String username) {
        List<Expense> expenses = readExpenses();
        expense.setUsername(username);
        expense.setId(nextId(expenses));
        expenses.add(expense);
        writeExpenses(expenses);
        return expense;
    }

    public void update(Long id, Expense updatedExpense, String username) {
        List<Expense> expenses = readExpenses();
        for (int i = 0; i < expenses.size(); i++) {
            Expense existingExpense = expenses.get(i);
            if (existingExpense.getId().equals(id) && existingExpense.getUsername().equalsIgnoreCase(username)) {
                updatedExpense.setId(id);
                updatedExpense.setUsername(username);
                expenses.set(i, updatedExpense);
                writeExpenses(expenses);
                return;
            }
        }
        throw new IllegalArgumentException("Expense not found");
    }

    public void delete(Long id, String username) {
        List<Expense> expenses = readExpenses();
        boolean removed = expenses.removeIf(expense -> expense.getId().equals(id) && expense.getUsername().equalsIgnoreCase(username));
        if (removed) {
            writeExpenses(expenses);
        }
    }

    public BigDecimal calculateTotal(String username) {
        return findByUsername(username).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Expense> recentExpenses(String username) {
        return findByUsername(username).stream()
                .limit(5)
                .toList();
    }

    public Map<Category, BigDecimal> categorySummary(String username) {
        Map<Category, BigDecimal> summary = new LinkedHashMap<>();
        for (Category category : Category.values()) {
            summary.put(category, BigDecimal.ZERO);
        }
        Map<Category, BigDecimal> totals = findByUsername(username).stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));
        summary.putAll(totals);
        return summary;
    }

    private boolean contains(String value, String searchText) {
        return value != null && value.toLowerCase().contains(searchText);
    }

    private Long nextId(List<Expense> expenses) {
        return expenses.stream()
                .map(Expense::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private List<Expense> readExpenses() {
        try {
            if (Files.notExists(expensesFilePath) || Files.size(expensesFilePath) == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(expensesFilePath.toFile(), new TypeReference<List<Expense>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read expenses.json", e);
        }
    }

    private void writeExpenses(List<Expense> expenses) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(expensesFilePath.toFile(), expenses);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write expenses.json", e);
        }
    }
}
