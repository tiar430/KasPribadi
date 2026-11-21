package com.tiar.kas.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MoneyTransaction> __insertionAdapterOfMoneyTransaction;

  private final EntityDeletionOrUpdateAdapter<MoneyTransaction> __deletionAdapterOfMoneyTransaction;

  private final EntityDeletionOrUpdateAdapter<MoneyTransaction> __updateAdapterOfMoneyTransaction;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMoneyTransaction = new EntityInsertionAdapter<MoneyTransaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `transactions` (`id`,`date`,`type`,`amount`,`accountId`,`toAccountId`,`categoryId`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MoneyTransaction entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDate());
        statement.bindString(3, __TransactionType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getAmount());
        statement.bindLong(5, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCategoryId());
        }
        statement.bindString(8, entity.getNote());
      }
    };
    this.__deletionAdapterOfMoneyTransaction = new EntityDeletionOrUpdateAdapter<MoneyTransaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MoneyTransaction entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMoneyTransaction = new EntityDeletionOrUpdateAdapter<MoneyTransaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`date` = ?,`type` = ?,`amount` = ?,`accountId` = ?,`toAccountId` = ?,`categoryId` = ?,`note` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MoneyTransaction entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDate());
        statement.bindString(3, __TransactionType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getAmount());
        statement.bindLong(5, entity.getAccountId());
        if (entity.getToAccountId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getToAccountId());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getCategoryId());
        }
        statement.bindString(8, entity.getNote());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MoneyTransaction tx, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMoneyTransaction.insertAndReturnId(tx);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MoneyTransaction tx, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMoneyTransaction.handle(tx);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MoneyTransaction tx, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMoneyTransaction.handle(tx);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object countByAccount(final long id, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(id) FROM transactions WHERE accountId = ? OR toAccountId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    _argIndex = 2;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super MoneyTransaction> $completion) {
    final String _sql = "SELECT id, date, type, amount, accountId, toAccountId, categoryId, note FROM transactions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MoneyTransaction>() {
      @Override
      @Nullable
      public MoneyTransaction call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfDate = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfAmount = 3;
          final int _cursorIndexOfAccountId = 4;
          final int _cursorIndexOfToAccountId = 5;
          final int _cursorIndexOfCategoryId = 6;
          final int _cursorIndexOfNote = 7;
          final MoneyTransaction _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final long _tmpAccountId;
            _tmpAccountId = _cursor.getLong(_cursorIndexOfAccountId);
            final Long _tmpToAccountId;
            if (_cursor.isNull(_cursorIndexOfToAccountId)) {
              _tmpToAccountId = null;
            } else {
              _tmpToAccountId = _cursor.getLong(_cursorIndexOfToAccountId);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _result = new MoneyTransaction(_tmpId,_tmpDate,_tmpType,_tmpAmount,_tmpAccountId,_tmpToAccountId,_tmpCategoryId,_tmpNote);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TransactionRow>> observeRows() {
    final String _sql = "\n"
            + "\t\tSELECT t.id, t.date, t.type, t.amount, t.note,\n"
            + "\t\t\ta.name AS accountName,\n"
            + "\t\t\tta.name AS toAccountName,\n"
            + "\t\t\tc.name AS categoryName\n"
            + "\t\tFROM transactions t\n"
            + "\t\tJOIN accounts a\t\t\tON a.id = t.accountId\n"
            + "\t\tLEFT JOIN accounts ta\tON ta.id = t.toAccountId\n"
            + "\t\tLEFT JOIN categories c\tON c.id = t.categoryId\n"
            + "\t\tORDER BY t.date DESC, t.id DESC\n"
            + "\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions", "accounts",
        "categories"}, new Callable<List<TransactionRow>>() {
      @Override
      @NonNull
      public List<TransactionRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfDate = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfAmount = 3;
          final int _cursorIndexOfNote = 4;
          final int _cursorIndexOfAccountName = 5;
          final int _cursorIndexOfToAccountName = 6;
          final int _cursorIndexOfCategoryName = 7;
          final List<TransactionRow> _result = new ArrayList<TransactionRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionRow _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final String _tmpAccountName;
            _tmpAccountName = _cursor.getString(_cursorIndexOfAccountName);
            final String _tmpToAccountName;
            if (_cursor.isNull(_cursorIndexOfToAccountName)) {
              _tmpToAccountName = null;
            } else {
              _tmpToAccountName = _cursor.getString(_cursorIndexOfToAccountName);
            }
            final String _tmpCategoryName;
            if (_cursor.isNull(_cursorIndexOfCategoryName)) {
              _tmpCategoryName = null;
            } else {
              _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            }
            _item = new TransactionRow(_tmpId,_tmpDate,_tmpType,_tmpAmount,_tmpNote,_tmpAccountName,_tmpToAccountName,_tmpCategoryName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionRow>> observeRowsBetween(final long start, final long end) {
    final String _sql = "\n"
            + "\t\tSELECT t.id, t.date, t.type, t.amount, t.note,\n"
            + "\t\t\ta.name AS accountName,\n"
            + "\t\t\tta.name AS toAccountName,\n"
            + "\t\t\tc.name AS categoryName\n"
            + "\t\tFROM transactions t\n"
            + "\t\tJOIN accounts a\t\t\tON a.id = t.accountId\n"
            + "\t\tLEFT JOIN accounts ta\tON ta.id = t.toAccountId\n"
            + "\t\tLEFT JOIN categories c\tON c.id = t.categoryId\n"
            + "\t\tWHERE t.date >= ? AND t.date < ?\n"
            + "\t\tORDER BY t.date DESC, t.id DESC\n"
            + "\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions", "accounts",
        "categories"}, new Callable<List<TransactionRow>>() {
      @Override
      @NonNull
      public List<TransactionRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfDate = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfAmount = 3;
          final int _cursorIndexOfNote = 4;
          final int _cursorIndexOfAccountName = 5;
          final int _cursorIndexOfToAccountName = 6;
          final int _cursorIndexOfCategoryName = 7;
          final List<TransactionRow> _result = new ArrayList<TransactionRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionRow _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final String _tmpAccountName;
            _tmpAccountName = _cursor.getString(_cursorIndexOfAccountName);
            final String _tmpToAccountName;
            if (_cursor.isNull(_cursorIndexOfToAccountName)) {
              _tmpToAccountName = null;
            } else {
              _tmpToAccountName = _cursor.getString(_cursorIndexOfToAccountName);
            }
            final String _tmpCategoryName;
            if (_cursor.isNull(_cursorIndexOfCategoryName)) {
              _tmpCategoryName = null;
            } else {
              _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            }
            _item = new TransactionRow(_tmpId,_tmpDate,_tmpType,_tmpAmount,_tmpNote,_tmpAccountName,_tmpToAccountName,_tmpCategoryName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<MonthSummary> observeSummaryBetween(final long start, final long end) {
    final String _sql = "\n"
            + "\t\tSELECT COALESCE(SUM(CASE WHEN type = 'PEMASUKAN' THEN amount ELSE 0 END), 0) AS income,\n"
            + "\t\t\tCOALESCE(SUM(CASE WHEN type = 'PENGELUARAN' THEN amount ELSE 0 END), 0) AS expense\n"
            + "\t\tFROM transactions\n"
            + "\t\tWHERE date >= ? AND date < ?\n"
            + "\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions"}, new Callable<MonthSummary>() {
      @Override
      @NonNull
      public MonthSummary call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIncome = 0;
          final int _cursorIndexOfExpense = 1;
          final MonthSummary _result;
          if (_cursor.moveToFirst()) {
            final long _tmpIncome;
            _tmpIncome = _cursor.getLong(_cursorIndexOfIncome);
            final long _tmpExpense;
            _tmpExpense = _cursor.getLong(_cursorIndexOfExpense);
            _result = new MonthSummary(_tmpIncome,_tmpExpense);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TransactionRow>> observeSearch(final String query, final String type,
      final Long accountId, final Long categoryId) {
    final String _sql = "\n"
            + "\t\tSELECT t.id, t.date, t.type, t.amount, t.note,\n"
            + "\t\t\ta.name AS accountName,\n"
            + "\t\t\tta.name AS toAccountName,\n"
            + "\t\t\tc.name AS categoryName\n"
            + "\t\tFROM transactions t\n"
            + "\t\tJOIN accounts a ON a.id = t.accountId\n"
            + "\t\tLEFT JOIN accounts ta ON ta.id = t.toAccountId\n"
            + "\t\tLEFT JOIN categories c ON c.id = t.categoryId\n"
            + "\t\tLEFT JOIN categories pc ON pc.id = c.parentId\n"
            + "\t\tWHERE (? IS NULL OR t.type = ?)\n"
            + "\t\tAND (? IS NULL OR t.accountId = ? OR t.toAccountId = ?)\n"
            + "\t\tAND (? IS NULL OR t.categoryId = ? OR c.parentId = ?)\n"
            + "\t\tAND (? = ''\n"
            + "\t\t  \tOR t.note LIKE '%' || ? || '%'\n"
            + "\t\t  \tOR c.name LIKE '%' || ? || '%'\n"
            + "\t\t  \tOR pc.name LIKE '%' || ? || '%'\n"
            + "\t\t  \tOR a.name LIKE '%' || ? || '%'\n"
            + "\t\t  \tOR CAST(t.amount AS TEXT) = ?)\n"
            + "\t\tORDER BY t.date DESC, t.id DESC\n"
            + "\t\tLIMIT 500\n"
            + "\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 14);
    int _argIndex = 1;
    if (type == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, type);
    }
    _argIndex = 2;
    if (type == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, type);
    }
    _argIndex = 3;
    if (accountId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, accountId);
    }
    _argIndex = 4;
    if (accountId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, accountId);
    }
    _argIndex = 5;
    if (accountId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, accountId);
    }
    _argIndex = 6;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    _argIndex = 7;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    _argIndex = 8;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    _argIndex = 9;
    _statement.bindString(_argIndex, query);
    _argIndex = 10;
    _statement.bindString(_argIndex, query);
    _argIndex = 11;
    _statement.bindString(_argIndex, query);
    _argIndex = 12;
    _statement.bindString(_argIndex, query);
    _argIndex = 13;
    _statement.bindString(_argIndex, query);
    _argIndex = 14;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions", "accounts",
        "categories"}, new Callable<List<TransactionRow>>() {
      @Override
      @NonNull
      public List<TransactionRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfDate = 1;
          final int _cursorIndexOfType = 2;
          final int _cursorIndexOfAmount = 3;
          final int _cursorIndexOfNote = 4;
          final int _cursorIndexOfAccountName = 5;
          final int _cursorIndexOfToAccountName = 6;
          final int _cursorIndexOfCategoryName = 7;
          final List<TransactionRow> _result = new ArrayList<TransactionRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TransactionRow _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final TransactionType _tmpType;
            _tmpType = __TransactionType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final String _tmpAccountName;
            _tmpAccountName = _cursor.getString(_cursorIndexOfAccountName);
            final String _tmpToAccountName;
            if (_cursor.isNull(_cursorIndexOfToAccountName)) {
              _tmpToAccountName = null;
            } else {
              _tmpToAccountName = _cursor.getString(_cursorIndexOfToAccountName);
            }
            final String _tmpCategoryName;
            if (_cursor.isNull(_cursorIndexOfCategoryName)) {
              _tmpCategoryName = null;
            } else {
              _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
            }
            _item = new TransactionRow(_tmpId,_tmpDate,_tmpType,_tmpAmount,_tmpNote,_tmpAccountName,_tmpToAccountName,_tmpCategoryName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CategoryTotal>> observeCategoryTotals(final String type, final long start,
      final long end) {
    final String _sql = "\n"
            + "\t\tSELECT COALESCE(p.id, c.id, -1) AS categoryId,\n"
            + "\t\t\tCOALESCE(p.name, c.name, 'Tanpa kategori') AS name,\n"
            + "\t\t\tSUM(t.amount) AS total\n"
            + "\t\tFROM transactions t\n"
            + "\t\tLEFT JOIN categories c ON c.id = t.categoryId\n"
            + "\t\tLEFT JOIN categories p ON p.id = c.parentId\n"
            + "\t\tWHERE t.type = ?\n"
            + "\t\t\tAND t.date >= ? AND t.date < ?\n"
            + "\t\tGROUP BY COALESCE(p.id, c.id, -1)\n"
            + "\t\tORDER BY total DESC\n"
            + "\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    _argIndex = 2;
    _statement.bindLong(_argIndex, start);
    _argIndex = 3;
    _statement.bindLong(_argIndex, end);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions",
        "categories"}, new Callable<List<CategoryTotal>>() {
      @Override
      @NonNull
      public List<CategoryTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfTotal = 2;
          final List<CategoryTotal> _result = new ArrayList<CategoryTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryTotal _item;
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpTotal;
            _tmpTotal = _cursor.getLong(_cursorIndexOfTotal);
            _item = new CategoryTotal(_tmpCategoryId,_tmpName,_tmpTotal);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CategoryTotal>> observeSubcategoryTotals(final String type, final long parentId,
      final long start, final long end) {
    final String _sql = "\n"
            + "\t\tSELECT c.id AS categoryId, c.name AS name, SUM(t.amount) AS total\n"
            + "\t\tFROM transactions t\n"
            + "\t\tJOIN categories c ON c.id = t.categoryId\n"
            + "\t\tWHERE t.type = ?\n"
            + "\t\t\tAND t.date >= ?\n"
            + "\t\t\tAND t.date < ?\n"
            + "\t\t\tAND (c.id = ? OR c.parentId = ?)\n"
            + "\t\tGROUP BY c.id\n"
            + "\t\tORDER BY total DESC\n"
            + "\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 5);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    _argIndex = 2;
    _statement.bindLong(_argIndex, start);
    _argIndex = 3;
    _statement.bindLong(_argIndex, end);
    _argIndex = 4;
    _statement.bindLong(_argIndex, parentId);
    _argIndex = 5;
    _statement.bindLong(_argIndex, parentId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions",
        "categories"}, new Callable<List<CategoryTotal>>() {
      @Override
      @NonNull
      public List<CategoryTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfTotal = 2;
          final List<CategoryTotal> _result = new ArrayList<CategoryTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryTotal _item;
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpTotal;
            _tmpTotal = _cursor.getLong(_cursorIndexOfTotal);
            _item = new CategoryTotal(_tmpCategoryId,_tmpName,_tmpTotal);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MonthTotal>> observeMonthlyTotals(final String type, final Long categoryId,
      final long start, final long end) {
    final String _sql = "\n"
            + "\t\tSELECT strftime('%Y-%m', t.date / 1000, 'unixepoch', 'localtime') AS ym, SUM(t.amount) AS total\n"
            + "\t\tFROM transactions t\n"
            + "\t\tLEFT JOIN categories c ON c.id = t.categoryId\n"
            + "\t\tWHERE t.type = ?\n"
            + "\t\t\tAND t.date >= ?\n"
            + "\t\t\tAND t.date < ?\n"
            + "\t\t\tAND (? IS NULL OR c.id = ? OR c.parentId = ?)\n"
            + "\t\tGROUP BY ym\n"
            + "\t\tORDER BY ym\n"
            + "\t";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    _argIndex = 2;
    _statement.bindLong(_argIndex, start);
    _argIndex = 3;
    _statement.bindLong(_argIndex, end);
    _argIndex = 4;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    _argIndex = 5;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    _argIndex = 6;
    if (categoryId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, categoryId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions",
        "categories"}, new Callable<List<MonthTotal>>() {
      @Override
      @NonNull
      public List<MonthTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYm = 0;
          final int _cursorIndexOfTotal = 1;
          final List<MonthTotal> _result = new ArrayList<MonthTotal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonthTotal _item;
            final String _tmpYm;
            _tmpYm = _cursor.getString(_cursorIndexOfYm);
            final long _tmpTotal;
            _tmpTotal = _cursor.getLong(_cursorIndexOfTotal);
            _item = new MonthTotal(_tmpYm,_tmpTotal);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __TransactionType_enumToString(@NonNull final TransactionType _value) {
    switch (_value) {
      case PEMASUKAN: return "PEMASUKAN";
      case PENGELUARAN: return "PENGELUARAN";
      case TRANSFER: return "TRANSFER";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private TransactionType __TransactionType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "PEMASUKAN": return TransactionType.PEMASUKAN;
      case "PENGELUARAN": return TransactionType.PENGELUARAN;
      case "TRANSFER": return TransactionType.TRANSFER;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
