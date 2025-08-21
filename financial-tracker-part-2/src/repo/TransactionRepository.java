package repo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.business.data.store.search.Filter;
import ch.ivyteam.ivy.environment.Ivy;
import entity.Transaction;
import entity.Transaction.Category;
import entity.Transaction.Type;
import entity.TransactionSearchCriteria;

/**
 * Repository class for managing {@link Transaction} entities.
 * Provides basic CRUD operations and search by criteria.
 */
public class TransactionRepository {

  private static final String FIELD_AMOUNT = "amount";
  private static final String FIELD_TYPE = "type";
  private static final String FIELD_CATEGORY = "category";
  private static final String FIELD_DESCRIPTION = "description";
  private static final String FIELD_DATE = "date";

  private static TransactionRepository instance;

  public static TransactionRepository getInstance() {
    if (instance == null) {
      instance = new TransactionRepository();
    }
    return instance;
  }

  /**
   * Creates and saves a new transaction.
   *
   * @param transaction the transaction to save
   * @return the persisted transaction
   * @throws IllegalArgumentException if the transaction is null
   */
  public Transaction create(Transaction transaction) {
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null");
    }

    Ivy.repo().save(transaction);
    return transaction;
  }

  /**
   * Retrieves all transactions.
   *
   * @return list of all transactions
   */
  public List<Transaction> findAll() {
    return Ivy.repo().search(Transaction.class).execute().getAll();
  }
  
  /**
   * Updates an existing transaction.
   *
   * @param transaction the transaction to update
   */
  public void update(Transaction transaction) {
    if (transaction == null) {
      return;
    }

    Ivy.repo().save(transaction);
  }

  /**
   * Deletes a transaction.
   *
   * @param transaction the transaction to delete, ignored if not exist
   */
  public void delete(Transaction transaction) {
    if (transaction == null) {
      return;
    }

    Ivy.repo().delete(transaction);
  }

  /**
   * Searches transactions based on the provided criteria.
   * Returns all transactions if criteria is null or has no filters.
   * Applies filters for amount, type, category, description, and date range (toDate inclusive).
   */
  public List<Transaction> findBySearchCriteria(TransactionSearchCriteria criteria) {
    if (criteria == null || !criteria.hasAnyFilter()) {
      return findAll();
    }

    var search = Ivy.repo().search(Transaction.class);

    // Collect filters
    List<Filter<Transaction>> filters = new ArrayList<>();

    // Amount range
    if (criteria.getMinAmount() != null) {
      filters.add(search.numberField(FIELD_AMOUNT).isGreaterOrEqualTo(criteria.getMinAmount()));
    }
    if (criteria.getMaxAmount() != null) {
      // max should be lessOrEqualTo
      filters.add(search.numberField(FIELD_AMOUNT).isLessOrEqualTo(criteria.getMaxAmount()));
    }

    // Type & category (enums)
    if (criteria.getType() != null && criteria.getType() != Type.NONE) {
      filters.add(search.textField(FIELD_TYPE).isEqualToIgnoringCase(criteria.getType().name()));
    }
    if (criteria.getCategory() != null && criteria.getCategory() != Category.NONE) {
      filters.add(search.textField(FIELD_CATEGORY).isEqualToIgnoringCase(criteria.getCategory().name()));
    }

    // Description full-text (guard blank)
    if (StringUtils.isNotBlank(criteria.getDescriptionContains())) {
      filters.add(search.textField(FIELD_DESCRIPTION).containsAllWordPatterns(criteria.getDescriptionContains()));
    }

    // Date range (from inclusive start-of-day, to inclusive end-of-day)
    if (criteria.getFromDate() != null) {
      filters.add(search.dateTimeField(FIELD_DATE).isAfter(startOfDay(criteria.getFromDate())));
    }
    if (criteria.getToDate() != null) {
      filters.add(search.dateTimeField(FIELD_DATE).isBefore(endOfDay(criteria.getToDate())));
    }

    // Apply collected filters
    for (Filter<Transaction> f : filters) {
      // chaining .and() after filter is acceptable in current API usage
      search.filter(f).and();
    }

    // TODO: apply pagination/sorting if criteria contains them
    // e.g. if (criteria.hasPaging()) search.range(criteria.getOffset(), criteria.getLimit());

    return search.execute().getAll();
  }


  /** Convert LocalDate to Date at start of day in system default zone. */
  private Date startOfDay(LocalDate localDate) {
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDate.atStartOfDay(zone).toInstant();
    return Date.from(instant);
  }

  /** Convert LocalDate to Date at end of day (23:59:59.999) in system default zone. */
  private Date endOfDay(LocalDate localDate) {
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDate.plusDays(1).atStartOfDay(zone).toInstant().minusMillis(1);
    return Date.from(instant);
  }

}
