package net.programmierecke.radiodroid2.players.exoplayer;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

import okhttp3.OkHttpClient;

public class RadioDataSourceFactory implements DataSource.Factory {

    private OkHttpClient httpClient;
    private final TransferListener<? super HttpDataSource> transferListener;
    private IcyDataSource.IcyDataSourceListener dataSourceListener;

    public RadioDataSourceFactory(OkHttpClient httpClient, TransferListener<? super HttpDataSource> transferListener, IcyDataSource.IcyDataSourceListener dataSourceListener) {
        this.httpClient = httpClient;
        this.transferListener = transferListener;
        this.dataSourceListener = dataSourceListener;
    }

    @Override
    public DataSource createDataSource() {
        return new IcyDataSource(httpClient, transferListener, dataSourceListener);
    }
}
