package net.micode.notes.model;

import android.content.ContentProviderOperation;//批量的更新、插入、删除数据。
import android.content.ContentProviderResult;//操作的结果
import android.content.ContentUris;//用于添加和获取Uri后面的ID
import android.content.ContentValues;//一种用来存储基本数据类型数据的存储机制
import android.content.Context;//需要用该类来弄清楚调用者的实例
import android.content.OperationApplicationException;//操作应用程序容错
import android.net.Uri;//表示待操作的数据
import android.os.RemoteException;//远程容错
import android.util.Log;//输出日志，比如说出错、警告等

public class Note {
// private ContentValues mNoteDiffValues;
ContentValues mNoteDiffValues;//
private NoteData mNoteData;
private static final String TAG = "Note";

/**
* Create a new note id for adding a new note to databases
*/
public static synchronized long getNewNoteId(Context context, long folderId) {
// Create a new note in the database
ContentValues values = new ContentValues();
long createdTime = System.currentTimeMillis();
values.put(NoteColumns.CREATED_DATE, createdTime);
values.put(NoteColumns.MODIFIED_DATE, createdTime);
values.put(NoteColumns.TYPE, Notes.TYPE_NOTE);
values.put(NoteColumns.LOCAL_MODIFIED, 1);
values.put(NoteColumns.PARENT_ID, folderId);//将数据写入数据库表格
Uri uri = context.getContentResolver().insert(Notes.CONTENT_NOTE_URI, values);
//ContentResolver()主要是实现外部应用对ContentProvider中的数据
//进行添加、删除、修改和查询操作
long noteId = 0;
try {
noteId = Long.valueOf(uri.getPathSegments().get(1));
} catch (NumberFormatException e) {
Log.e(TAG, "Get note id error :" + e.toString());
noteId = 0;
}//try-catch异常处理
if (noteId == -1) {
throw new IllegalStateException("Wrong note id:" + noteId);
}
return noteId;
}

public Note() {
mNoteDiffValues = new ContentValues();
mNoteData = new NoteData();
}//定义两个变量用来存储便签的数据，一个是存储便签属性、一个是存储便签内容

public void setNoteValue(String key, String value) {
mNoteDiffValues.put(key, value);
mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1);
mNoteDiffValues.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
}//设置数据库表格的标签属性数据

public void setTextData(String key, String value) {
mNoteData.setTextData(key, value);
}//设置数据库表格的标签文本内容的数据

public void setTextDataId(long id) {
mNoteData.setTextDataId(id);
}//设置文本数据的ID

public long getTextDataId() {
return mNoteData.mTextDataId;
}//得到文本数据的ID

public void setCallDataId(long id) {
mNoteData.setCallDataId(id);
}//设置电话号码数据的ID

public void setCallData(String key, String value) {
mNoteData.setCallData(key, value);
}//得到电话号码数据的ID

public boolean isLocalModified() {
return mNoteDiffValues.size() > 0 || mNoteData.isLocalModified();
}//判断是否是本地修改

public boolean syncNote(Context context, long noteId) {
if (noteId <= 0) { throw new IllegalArgumentException("Wrong note id:" + noteId); } if (!isLocalModified()) { return
  true; } /** * In theory, once data changed, the note should be updated on {@link NoteColumns#LOCAL_MODIFIED} and *
  {@link NoteColumns#MODIFIED_DATE}. For data safety, though update note fails, we also update the * note data info */
  if (context.getContentResolver().update( ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId), mNoteDiffValues,
  null, null)==0) { Log.e(TAG, "Update note error, should not happen" ); // Do not return, fall through }
  mNoteDiffValues.clear(); if (mNoteData.isLocalModified() && (mNoteData.pushIntoContentResolver(context,
  noteId)==null)) { return false; } return true; }//判断数据是否同步 private class NoteData {//定义一个基本的便签内容的数据类，主要包含文本数据和电话号码数据
  private long mTextDataId; private ContentValues mTextDataValues;//文本数据 private long mCallDataId; private ContentValues
  mCallDataValues;//电话号码数据 private static final String TAG="NoteData" ; public NoteData() { mTextDataValues=new
  ContentValues(); mCallDataValues=new ContentValues(); mTextDataId=0; mCallDataId=0; } //下面是上述几个函数的具体实现 boolean
  isLocalModified() { return mTextDataValues.size()> 0 || mCallDataValues.size() > 0;
  }

  void setTextDataId(long id) {
  if(id <= 0) { throw new IllegalArgumentException("Text data id should larger than 0"); } mTextDataId=id; } void
    setCallDataId(long id) { if (id <=0) { throw new IllegalArgumentException("Call data id should larger than 0"); }
    mCallDataId=id; } void setCallData(String key, String value) { mCallDataValues.put(key, value);
    mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1); mNoteDiffValues.put(NoteColumns.MODIFIED_DATE,
    System.currentTimeMillis()); } void setTextData(String key, String value) { mTextDataValues.put(key, value);
    mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1); mNoteDiffValues.put(NoteColumns.MODIFIED_DATE,
    System.currentTimeMillis()); } //下面函数的作用是将新的数据通过Uri的操作存储到数据库 Uri pushIntoContentResolver(Context context, long
    noteId) 
    { /** * Check for safety */ if (noteId <=0) { throw new IllegalArgumentException("Wrong note id:" + noteId);
    }//判断数据是否合法 ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
      ContentProviderOperation.Builder builder = null;//数据库的操作列表
