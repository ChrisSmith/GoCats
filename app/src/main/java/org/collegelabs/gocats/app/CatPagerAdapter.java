package org.collegelabs.gocats.app;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.jakewharton.salvage.RecyclingPagerAdapter;
import go.libcats.Libcats;
import timber.log.Timber;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;


/**
 */
public class CatPagerAdapter extends RecyclingPagerAdapter {

    private ArrayList<ImageMetaData> metaDataList;
    private HashSet<String> ids;
    private Libcats.CallbackToken callbackToken;

    private Handler handler;

    private String _lastId; //set to null after used

    public CatPagerAdapter(String lastId){
        metaDataList = new ArrayList<ImageMetaData>();
        handler = new Handler();
        ids = new HashSet<String>();
        _lastId = lastId;
    }

    public void StopLoading(){
        if(callbackToken != null){
            callbackToken.Close();
            callbackToken = null;
        }
    }

    public String GetId(int position){
        if(position < 0 || position >= metaDataList.size()){
            return "";
        }
        return metaDataList.get(position).id;
    }

    public String GetLastId(){
        return GetId(metaDataList.size() - 1);
    }

    public void StartLoading(){
        StopLoading();

        String lastId;
        if(_lastId != null) {
            lastId = _lastId;
            _lastId = null;
        }else{
            lastId = GetLastId();
        }
        callbackToken = Libcats.CreateMetaDataCallback(new MetaDataCallback(this), lastId);
    }

    public void Add(ImageMetaData metaData){
        if(!ids.add(metaData.id)){
            return;
        }

        metaDataList.add(metaData);
        // TODO This sucks, because we don't have array support in gobind this will trash the views every change
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return metaDataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        if(position == getCount() - 1){
            Timber.d("LoadNextBatch");
            callbackToken.LoadNextBatch();
        }

        CatView catView;
        if(convertView != null){
            catView = (CatView) convertView;
        }else{
            catView = new CatView(container.getContext());
        }

        catView.setMetaData(metaDataList.get(position));

        return catView;
    }

    private static class MetaDataCallback extends Libcats.MetaDataCallback.Stub {

        private WeakReference<CatPagerAdapter> adapterRef;

        public MetaDataCallback(CatPagerAdapter adapter){
            adapterRef = new WeakReference<CatPagerAdapter>(adapter);
        }

        @Override
        public void MetaDataReceived(String url, String title, String author, String permalink, String id) {
            final CatPagerAdapter adapter = adapterRef.get();
            if(adapter == null){
                return;
            }

            final ImageMetaData metaData = new ImageMetaData(url, title, author, permalink, id);
            adapter.handler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.Add(metaData);
                }
            });
        }
    }

}
