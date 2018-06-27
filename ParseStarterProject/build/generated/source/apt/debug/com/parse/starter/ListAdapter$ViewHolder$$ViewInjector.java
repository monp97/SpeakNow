// Generated code from Butter Knife. Do not modify!
package com.parse.starter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class ListAdapter$ViewHolder$$ViewInjector<T extends com.parse.starter.ListAdapter.ViewHolder> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755026, "field 'text'");
    target.text = finder.castView(view, 2131755026, "field 'text'");
  }

  @Override public void reset(T target) {
    target.text = null;
  }
}
