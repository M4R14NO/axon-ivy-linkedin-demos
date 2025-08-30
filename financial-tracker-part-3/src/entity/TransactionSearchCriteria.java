package entity;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

import dev.langchain4j.internal.Json;
import dev.langchain4j.model.output.structured.Description;

/**
 * Entity class representing search criteria for filtering transactions.
 * This class is designed to work with LangChain4j for AI-powered natural language
 * parsing of search queries into structured criteria.
 */
public class TransactionSearchCriteria {

    @Description("Minimum amount for the transaction search (inclusive). Leave null if no minimum amount filter is needed.")
    private Double minAmount;

    @Description("Maximum amount for the transaction search (inclusive). Leave null if no maximum amount filter is needed.")
    private Double maxAmount;

    @Description("Type of transaction to search for: 'INCOME' or 'EXPENSE'. Leave null to search all types.")
    private Transaction.Type type;

    @Description("Category of transaction to search for (e.g., FOOD, DRINK, PARTY, CLOTHES, SALARY, INVESTMENT_INTEREST, OTHER). Leave null to search all categories.")
    private Transaction.Category category;

    @Description("Text to search for in transaction descriptions. Case-insensitive partial matching. Leave null if no description filter is needed.")
    private String descriptionContains;

    @Description("Start date for the search range (inclusive). Format: YYYY-MM-DD. Leave null if no start date filter is needed.")
    private LocalDate fromDate;

    @Description("End date for the search range (inclusive). Format: YYYY-MM-DD. Leave null if no end date filter is needed.")
    private LocalDate toDate;

    // Default constructor
    public TransactionSearchCriteria() {}

    // Getters and setters
    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Transaction.Type getType() {
        return type;
    }

    public void setType(Transaction.Type type) {
        this.type = type;
    }

    public Transaction.Category getCategory() {
        return category;
    }

    public void setCategory(Transaction.Category category) {
        this.category = category;
    }

    public String getDescriptionContains() {
        return descriptionContains;
    }

    public void setDescriptionContains(String descriptionContains) {
        this.descriptionContains = descriptionContains;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    /**
     * Checks if this search criteria has any filters set.
     * @return true if at least one search criterion is specified, false otherwise
     */
    public boolean hasAnyFilter() {
        return minAmount != null || 
               maxAmount != null || 
               type != null || 
               category != null || 
               StringUtils.isBlank(descriptionContains) ||
               fromDate != null || 
               toDate != null;
    }

    @Override
    public String toString() {
      return this == null ? "" : Json.toJson(this);
    }
}
