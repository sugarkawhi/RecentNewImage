# RecentNewImage

### 代码实现
+ 利用ContentProvider查找最新的一张图片进行显示

### 应用场景
+ 类似于QQ、微信等打开输入框提示你刚刚保存或拍摄的一张图片

### 逻辑
+ 取最新的一张图，获取到文件路径
+ 根据File的lastModified()方法，进行判断时间差。如果在提示范围，比如1分钟内，就提示；否则不予理睬

### 代码
```Java
public void getImage() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = MainActivity.this.getContentResolver();

        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");

        if (mCursor == null) {
            return;
        }

        //只取一个
        if (mCursor.moveToNext()) {
            //获取最新图片的路径
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            //获取该图片的父路径名
//        String parentName = new File(path).getParentFile().getName();
            File file = new File(path);
            long updatetime = file.lastModified();

            Log.e(TAG, "getImage: path " + path);
            Log.e(TAG, "getImage: updatetime " + updatetime);
            Log.e(TAG, "getImage: currenttime " + System.currentTimeMillis());
            //这里可以比较一下时间差 来判断是否提示有最新的照片

            //通知Handler扫描图片完成
            Message msg = Message.obtain();
            msg.obj = path;
            handler.sendMessage(msg);
            mCursor.close();
        }
    }

```

利用Handler更新UI
```
   Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "handleMessage: ");
            String path = (String) msg.obj;
            if (!TextUtils.isEmpty(path)) {
                Bitmap b = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(b);
            }
        }
    };

```
