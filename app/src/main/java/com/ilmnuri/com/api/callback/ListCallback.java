//package nizzle.com.ilmnuri.api.callback;
//
//import java.util.List;
//
//import retrofit.Callback;
//import retrofit.RetrofitError;
//import retrofit.client.Response;
//
///**
// * Created by User on 18.05.2016.
// */
//public abstract class ListCallback<T> implements Callback<ListResult<T>> {
//
//    @Override
//    public void success(ListResult<T> tListResult, Response response) {
//        if (tListResult.getResults() != null) {
//            success(tListResult.getResults());
//            if (tListResult.getResults().isEmpty()) {
//                emptyList();
//            }
//        } else {
//            emptyList();
//        }
//        complete();
//    }
//
//    @Override
//    public void failure(RetrofitError error) {
//        if (error.getKind() == RetrofitError.Kind.NETWORK) {
//            networkError();
//        }
//
//        error(error);
//        complete();
//    }
//
//    public abstract void success(List<T> result);
//
//    public void complete() {
//    }
//
//    public void error(Exception e) {
//    }
//
//    public void networkError() {
//    }
//
//    public void emptyList() {
//    }
//}
//
