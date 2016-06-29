package starter.kit.rx;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import starter.kit.model.entity.Entity;
import starter.kit.rx.app.RxStarterRecyclerFragment;
import starter.kit.rx.util.RxPager;
import starter.kit.rx.util.RxUtils;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class ResourcePresenter extends RxStarterPresenter<RxStarterRecyclerFragment> {

  private static final int RESTARTABLE_ID = 1;

  private PublishSubject<RxPager> pageRequests = PublishSubject.create();

  @SuppressWarnings("Unchecked")
  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    restartableReplay(RESTARTABLE_ID, new Func0<Observable<ArrayList<? extends Entity>>>() {
      @Override public Observable<ArrayList<? extends Entity>> call() {
        return view().concatMap(new Func1<RxStarterRecyclerFragment, Observable<ArrayList<? extends Entity>>>() {
          @Override public Observable<ArrayList<? extends Entity>> call(RxStarterRecyclerFragment fragment) {
            return pageRequests.startWith(fragment.getRxPager())
                .concatMap(new Func1<RxPager, Observable<ArrayList<? extends Entity>>>() {
                  @Override public Observable<ArrayList<? extends Entity>> call(RxPager page) {
                    Observable<ArrayList<? extends Entity>> observable = fragment.request(page.nextPage(), page.pageSize());
                    return observable.subscribeOn(io())
                        .delay(5, TimeUnit.SECONDS)
                        .compose(RxUtils.progressTransformer(fragment))
                        .observeOn(mainThread());
                  }
                });
          }
        });
      }
    }, new Action2<RxStarterRecyclerFragment, ArrayList<? extends Entity>>() {
      @Override public void call(RxStarterRecyclerFragment feedFragment, ArrayList<? extends Entity> feeds) {
        feedFragment.notifyDataSetChanged(feeds);
      }
    }, new Action2<RxStarterRecyclerFragment, Throwable>() {
      @Override public void call(RxStarterRecyclerFragment feedFragment, Throwable throwable) {
        feedFragment.onNetworkError(throwable);
      }
    });
  }

  public void request() {
    start(RESTARTABLE_ID);
  }

  public void requestNext(RxPager page) {
    pageRequests.onNext(page);
  }
}
