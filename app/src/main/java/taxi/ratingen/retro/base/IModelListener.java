package taxi.ratingen.retro.base;


import taxi.ratingen.utilz.exception.CustomException;

/**
 * Created by guru on 1/6/2017.
 */

public interface IModelListener<T> {

    void onSuccessfulApi(long taskId, T response);

    void onFailureApi(long taskId, CustomException e);
}
