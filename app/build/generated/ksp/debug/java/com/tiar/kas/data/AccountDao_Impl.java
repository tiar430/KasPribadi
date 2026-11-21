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
public final class AccountDao_Impl implements AccountDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Account> __insertionAdapterOfAccount;

  private final EntityDeletionOrUpdateAdapter<Account> __deletionAdapterOfAccount;

  private final EntityDeletionOrUpdateAdapter<Account> __updateAdapterOfAccount;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public AccountDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAccount = new EntityInsertionAdapter<Account>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `accounts` (`id`,`name`,`accountGroup`,`initialBalance`,`includeInTotal`,`sortOrder`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Account entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, __AccountGroup_enumToString(entity.getAccountGroup()));
        statement.bindLong(4, entity.getInitialBalance());
        final int _tmp = entity.getIncludeInTotal() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getSortOrder());
      }
    };
    this.__deletionAdapterOfAccount = new EntityDeletionOrUpdateAdapter<Account>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `accounts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Account entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAccount = new EntityDeletionOrUpdateAdapter<Account>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `accounts` SET `id` = ?,`name` = ?,`accountGroup` = ?,`initialBalance` = ?,`includeInTotal` = ?,`sortOrder` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Account entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, __AccountGroup_enumToString(entity.getAccountGroup()));
        statement.bindLong(4, entity.getInitialBalance());
        final int _tmp = entity.getIncludeInTotal() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getSortOrder());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM accounts WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Account account, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAccount.insertAndReturnId(account);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Account account, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAccount.handle(account);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Account account, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAccount.handle(account);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Flow<List<Account>> observeAll() {
    final String _sql = "SELECT id, name, accountGroup, initialBalance, includeInTotal, sortOrder FROM accounts ORDER BY sortOrder, id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"accounts"}, new Callable<List<Account>>() {
      @Override
      @NonNull
      public List<Account> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAccountGroup = 2;
          final int _cursorIndexOfInitialBalance = 3;
          final int _cursorIndexOfIncludeInTotal = 4;
          final int _cursorIndexOfSortOrder = 5;
          final List<Account> _result = new ArrayList<Account>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Account _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final AccountGroup _tmpAccountGroup;
            _tmpAccountGroup = __AccountGroup_stringToEnum(_cursor.getString(_cursorIndexOfAccountGroup));
            final long _tmpInitialBalance;
            _tmpInitialBalance = _cursor.getLong(_cursorIndexOfInitialBalance);
            final boolean _tmpIncludeInTotal;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIncludeInTotal);
            _tmpIncludeInTotal = _tmp != 0;
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _item = new Account(_tmpId,_tmpName,_tmpAccountGroup,_tmpInitialBalance,_tmpIncludeInTotal,_tmpSortOrder);
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
  public Flow<List<AccountBalance>> observeBalances() {
    final String _sql = "\n"
            + "        SELECT a.id, a.name, a.accountGroup, a.includeInTotal,\n"
            + "               a.initialBalance\n"
            + "               + COALESCE((SELECT SUM(CASE WHEN t.type = 'PEMASUKAN' THEN t.amount ELSE -t.amount END)\n"
            + "                           FROM transactions t\n"
            + "                           WHERE t.accountId = a.id), 0)\n"
            + "               + COALESCE((SELECT SUM(t.amount)\n"
            + "                           FROM transactions t\n"
            + "                           WHERE t.type = 'TRANSFER' AND t.toAccountId = a.id), 0)\n"
            + "               AS balance\n"
            + "        FROM accounts a\n"
            + "        ORDER BY a.sortOrder, a.id\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"transactions",
        "accounts"}, new Callable<List<AccountBalance>>() {
      @Override
      @NonNull
      public List<AccountBalance> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAccountGroup = 2;
          final int _cursorIndexOfIncludeInTotal = 3;
          final int _cursorIndexOfBalance = 4;
          final List<AccountBalance> _result = new ArrayList<AccountBalance>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AccountBalance _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final AccountGroup _tmpAccountGroup;
            _tmpAccountGroup = __AccountGroup_stringToEnum(_cursor.getString(_cursorIndexOfAccountGroup));
            final boolean _tmpIncludeInTotal;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIncludeInTotal);
            _tmpIncludeInTotal = _tmp != 0;
            final long _tmpBalance;
            _tmpBalance = _cursor.getLong(_cursorIndexOfBalance);
            _item = new AccountBalance(_tmpId,_tmpName,_tmpAccountGroup,_tmpIncludeInTotal,_tmpBalance);
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
  public Object getById(final long id, final Continuation<? super Account> $completion) {
    final String _sql = "SELECT id, name, accountGroup, initialBalance, includeInTotal, sortOrder FROM accounts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Account>() {
      @Override
      @Nullable
      public Account call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfAccountGroup = 2;
          final int _cursorIndexOfInitialBalance = 3;
          final int _cursorIndexOfIncludeInTotal = 4;
          final int _cursorIndexOfSortOrder = 5;
          final Account _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final AccountGroup _tmpAccountGroup;
            _tmpAccountGroup = __AccountGroup_stringToEnum(_cursor.getString(_cursorIndexOfAccountGroup));
            final long _tmpInitialBalance;
            _tmpInitialBalance = _cursor.getLong(_cursorIndexOfInitialBalance);
            final boolean _tmpIncludeInTotal;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIncludeInTotal);
            _tmpIncludeInTotal = _tmp != 0;
            final int _tmpSortOrder;
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
            _result = new Account(_tmpId,_tmpName,_tmpAccountGroup,_tmpInitialBalance,_tmpIncludeInTotal,_tmpSortOrder);
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
  public Object nextSortOrder(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM accounts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(id) FROM accounts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __AccountGroup_enumToString(@NonNull final AccountGroup _value) {
    switch (_value) {
      case TUNAI: return "TUNAI";
      case BANK: return "BANK";
      case E_WALLET: return "E_WALLET";
      case KARTU_KREDIT: return "KARTU_KREDIT";
      case INVESTASI: return "INVESTASI";
      case PINJAMAN: return "PINJAMAN";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private AccountGroup __AccountGroup_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "TUNAI": return AccountGroup.TUNAI;
      case "BANK": return AccountGroup.BANK;
      case "E_WALLET": return AccountGroup.E_WALLET;
      case "KARTU_KREDIT": return AccountGroup.KARTU_KREDIT;
      case "INVESTASI": return AccountGroup.INVESTASI;
      case "PINJAMAN": return AccountGroup.PINJAMAN;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
