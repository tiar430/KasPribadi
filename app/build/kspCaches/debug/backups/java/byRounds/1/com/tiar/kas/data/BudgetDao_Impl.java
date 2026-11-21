package com.tiar.kas.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
public final class BudgetDao_Impl implements BudgetDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByCategory;

  private final EntityUpsertionAdapter<Budget> __upsertionAdapterOfBudget;

  public BudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfDeleteByCategory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM budgets WHERE categoryId = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfBudget = new EntityUpsertionAdapter<Budget>(new EntityInsertionAdapter<Budget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `budgets` (`categoryId`,`amount`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Budget entity) {
        statement.bindLong(1, entity.getCategoryId());
        statement.bindLong(2, entity.getAmount());
      }
    }, new EntityDeletionOrUpdateAdapter<Budget>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `budgets` SET `categoryId` = ?,`amount` = ? WHERE `categoryId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Budget entity) {
        statement.bindLong(1, entity.getCategoryId());
        statement.bindLong(2, entity.getAmount());
        statement.bindLong(3, entity.getCategoryId());
      }
    });
  }

  @Override
  public Object deleteByCategory(final long categoryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByCategory.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, categoryId);
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
          __preparedStmtOfDeleteByCategory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final Budget budget, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfBudget.upsert(budget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Budget>> observeAll() {
    final String _sql = "SELECT categoryId, amount FROM budgets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"budgets"}, new Callable<List<Budget>>() {
      @Override
      @NonNull
      public List<Budget> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfAmount = 1;
          final List<Budget> _result = new ArrayList<Budget>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Budget _item;
            final long _tmpCategoryId;
            _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            _item = new Budget(_tmpCategoryId,_tmpAmount);
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
}
