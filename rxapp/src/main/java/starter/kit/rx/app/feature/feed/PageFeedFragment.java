package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import nucleus.factory.RequiresPresenter;
import starter.kit.app.StarterFragConfig;
import starter.kit.app.StarterRecyclerFragment;
import starter.kit.rx.app.R;
import starter.kit.rx.app.model.entity.Feed;

@RequiresPresenter(PageFeedPresenter.class)
public class PageFeedFragment extends StarterRecyclerFragment {

  public static PageFeedFragment create() {
    PageFeedFragment feedFragment = new PageFeedFragment();
    return feedFragment;
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    StarterFragConfig.Builder builder = new StarterFragConfig.Builder()
        .withIdentifierRequest(false)
        .pageSize(30)
        .loadingTriggerThreshold(0)
        .bind(Feed.class, FeedsViewHolder.class)
        .recyclerViewDecor(new HorizontalDividerItemDecoration
            .Builder(getContext()).size(10)
            .colorResId(R.color.dividerColor)
            .build())
        .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);

    buildFragConfig(builder.build());
  }
}