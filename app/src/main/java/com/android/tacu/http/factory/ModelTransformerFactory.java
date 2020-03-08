package com.android.tacu.http.factory;

import com.android.tacu.http.network.ApiStatus;
import com.android.tacu.base.BaseModel;
import com.android.tacu.http.exceptions.ResponseExceptionHandler;
import com.android.tacu.http.exceptions.ServerException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

public class ModelTransformerFactory {

    public ModelTransformerFactory() {
    }

    public static <T> ObservableTransformer<BaseModel<T>, T> getBaseModelTransformer() {
        return new ObservableTransformer<BaseModel<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<BaseModel<T>> baseModelObservable) {
                return baseModelObservable.map(new Function<BaseModel<T>, T>() {
                    @Override
                    public T apply(BaseModel<T> tBaseModel) {
                        if (tBaseModel.status != ApiStatus.SUCCESS_HTTPS) {
                            throw new ServerException(tBaseModel.status, tBaseModel.message);
                        } else {
                            return (T) tBaseModel;
                        }
                    }
                }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                        return Observable.<T>error(ResponseExceptionHandler.handleResponseException(throwable));
                    }
                });
            }
        };
    }

    public static <T> ObservableTransformer<T, T> getNonStandardModelTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> tObservable) {
                return tObservable.map(new Function<T, T>() {
                    @Override
                    public T apply(T t) {
                        return t;
                    }
                }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                        return Observable.error(ResponseExceptionHandler.handleResponseException(throwable));
                    }
                });
            }
        };
    }
}

