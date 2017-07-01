package io.realm;


import android.util.JsonReader;
import android.util.JsonToken;
import david.projectclouds.GlucoseData;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnType;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.LinkView;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Table;
import io.realm.internal.TableOrView;
import io.realm.internal.android.JsonUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GlucoseDataRealmProxy extends GlucoseData
    implements RealmObjectProxy {

    private static long INDEX_DATE;
    private static long INDEX_CONCENTRATION1;
    private static long INDEX_TIME1;
    private static Map<String, Long> columnIndices;
    private static final List<String> FIELD_NAMES;
    static {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("date");
        fieldNames.add("concentration1");
        fieldNames.add("time1");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    @Override
    public String getDate() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_DATE);
    }

    @Override
    public void setDate(String value) {
        realm.checkIfValid();
        row.setString(INDEX_DATE, (String) value);
    }

    @Override
    public String getConcentration1() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_CONCENTRATION1);
    }

    @Override
    public void setConcentration1(String value) {
        realm.checkIfValid();
        row.setString(INDEX_CONCENTRATION1, (String) value);
    }

    @Override
    public String getTime1() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(INDEX_TIME1);
    }

    @Override
    public void setTime1(String value) {
        realm.checkIfValid();
        row.setString(INDEX_TIME1, (String) value);
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (!transaction.hasTable("class_GlucoseData")) {
            Table table = transaction.getTable("class_GlucoseData");
            table.addColumn(ColumnType.STRING, "date");
            table.addColumn(ColumnType.STRING, "concentration1");
            table.addColumn(ColumnType.STRING, "time1");
            table.addSearchIndex(table.getColumnIndex("date"));
            table.setPrimaryKey("date");
            return table;
        }
        return transaction.getTable("class_GlucoseData");
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_GlucoseData")) {
            Table table = transaction.getTable("class_GlucoseData");
            if (table.getColumnCount() != 3) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field count does not match - expected 3 but was " + table.getColumnCount());
            }
            Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
            for (long i = 0; i < 3; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }

            columnIndices = new HashMap<String, Long>();
            for (String fieldName : getFieldNames()) {
                long index = table.getColumnIndex(fieldName);
                if (index == -1) {
                    throw new RealmMigrationNeededException(transaction.getPath(), "Field '" + fieldName + "' not found for type GlucoseData");
                }
                columnIndices.put(fieldName, index);
            }
            INDEX_DATE = table.getColumnIndex("date");
            INDEX_CONCENTRATION1 = table.getColumnIndex("concentration1");
            INDEX_TIME1 = table.getColumnIndex("time1");

            if (!columnTypes.containsKey("date")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'date'");
            }
            if (columnTypes.get("date") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'date'");
            }
            if (table.getPrimaryKey() != table.getColumnIndex("date")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Primary key not defined for field 'date'");
            }
            if (!table.hasSearchIndex(table.getColumnIndex("date"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'date'");
            }
            if (!columnTypes.containsKey("concentration1")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'concentration1'");
            }
            if (columnTypes.get("concentration1") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'concentration1'");
            }
            if (!columnTypes.containsKey("time1")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'time1'");
            }
            if (columnTypes.get("time1") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'time1'");
            }
        } else {
            throw new RealmMigrationNeededException(transaction.getPath(), "The GlucoseData class is missing from the schema for this Realm.");
        }
    }

    public static String getTableName() {
        return "class_GlucoseData";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    public static Map<String,Long> getColumnIndices() {
        return columnIndices;
    }

    public static GlucoseData createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update)
        throws JSONException {
        GlucoseData obj = null;
        if (update) {
            Table table = realm.getTable(GlucoseData.class);
            long pkColumnIndex = table.getPrimaryKey();
            if (!json.isNull("date")) {
                long rowIndex = table.findFirstString(pkColumnIndex, json.getString("date"));
                if (rowIndex != TableOrView.NO_MATCH) {
                    obj = new GlucoseDataRealmProxy();
                    obj.realm = realm;
                    obj.row = table.getUncheckedRow(rowIndex);
                }
            }
        }
        if (obj == null) {
            obj = realm.createObject(GlucoseData.class);
        }
        if (json.has("date")) {
            if (json.isNull("date")) {
                obj.setDate("");
            } else {
                obj.setDate((String) json.getString("date"));
            }
        }
        if (json.has("concentration1")) {
            if (json.isNull("concentration1")) {
                obj.setConcentration1("");
            } else {
                obj.setConcentration1((String) json.getString("concentration1"));
            }
        }
        if (json.has("time1")) {
            if (json.isNull("time1")) {
                obj.setTime1("");
            } else {
                obj.setTime1((String) json.getString("time1"));
            }
        }
        return obj;
    }

    public static GlucoseData createUsingJsonStream(Realm realm, JsonReader reader)
        throws IOException {
        GlucoseData obj = realm.createObject(GlucoseData.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("date")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setDate("");
                    reader.skipValue();
                } else {
                    obj.setDate((String) reader.nextString());
                }
            } else if (name.equals("concentration1")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setConcentration1("");
                    reader.skipValue();
                } else {
                    obj.setConcentration1((String) reader.nextString());
                }
            } else if (name.equals("time1")) {
                if (reader.peek() == JsonToken.NULL) {
                    obj.setTime1("");
                    reader.skipValue();
                } else {
                    obj.setTime1((String) reader.nextString());
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return obj;
    }

    public static GlucoseData copyOrUpdate(Realm realm, GlucoseData object, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        if (object.realm != null && object.realm.getPath().equals(realm.getPath())) {
            return object;
        }
        GlucoseData realmObject = null;
        boolean canUpdate = update;
        if (canUpdate) {
            Table table = realm.getTable(GlucoseData.class);
            long pkColumnIndex = table.getPrimaryKey();
            if (object.getDate() == null) {
                throw new IllegalArgumentException("Primary key value must not be null.");
            }
            long rowIndex = table.findFirstString(pkColumnIndex, object.getDate());
            if (rowIndex != TableOrView.NO_MATCH) {
                realmObject = new GlucoseDataRealmProxy();
                realmObject.realm = realm;
                realmObject.row = table.getUncheckedRow(rowIndex);
                cache.put(object, (RealmObjectProxy) realmObject);
            } else {
                canUpdate = false;
            }
        }

        if (canUpdate) {
            return update(realm, realmObject, object, cache);
        } else {
            return copy(realm, object, update, cache);
        }
    }

    public static GlucoseData copy(Realm realm, GlucoseData newObject, boolean update, Map<RealmObject,RealmObjectProxy> cache) {
        GlucoseData realmObject = realm.createObject(GlucoseData.class, newObject.getDate());
        cache.put(newObject, (RealmObjectProxy) realmObject);
        realmObject.setDate(newObject.getDate() != null ? newObject.getDate() : "");
        realmObject.setConcentration1(newObject.getConcentration1() != null ? newObject.getConcentration1() : "");
        realmObject.setTime1(newObject.getTime1() != null ? newObject.getTime1() : "");
        return realmObject;
    }

    static GlucoseData update(Realm realm, GlucoseData realmObject, GlucoseData newObject, Map<RealmObject, RealmObjectProxy> cache) {
        realmObject.setConcentration1(newObject.getConcentration1() != null ? newObject.getConcentration1() : "");
        realmObject.setTime1(newObject.getTime1() != null ? newObject.getTime1() : "");
        return realmObject;
    }

    @Override
    public String toString() {
        if (!isValid()) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("GlucoseData = [");
        stringBuilder.append("{date:");
        stringBuilder.append(getDate());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{concentration1:");
        stringBuilder.append(getConcentration1());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{time1:");
        stringBuilder.append(getTime1());
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        String realmName = realm.getPath();
        String tableName = row.getTable().getName();
        long rowIndex = row.getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlucoseDataRealmProxy aGlucoseData = (GlucoseDataRealmProxy)o;

        String path = realm.getPath();
        String otherPath = aGlucoseData.realm.getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = row.getTable().getName();
        String otherTableName = aGlucoseData.row.getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        if (row.getIndex() != aGlucoseData.row.getIndex()) return false;

        return true;
    }

}
