package id.hipe.base;

import android.arch.persistence.room.*;

import java.util.List;

@Dao
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertIgnore(T ts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T ts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(List<T> ts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> ts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(List<T> ts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(T ts);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    int updateIgnore(T ts);

    @Delete
    void delete(List<T> ts);

    @Delete
    void delete(T ts);
}
