package com.fanfou.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StatusHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * 
 */
public class StatusCursorAdapter extends BaseCursorAdapter {
	private static final int ITEM_TYPE_MENTION = 0;
	private static final int ITEM_TYPE_NONE = 1;
	private static final int[] viewTypes = new int[] { ITEM_TYPE_MENTION,
			ITEM_TYPE_NONE };

	private boolean colored;

	@Override
	public int getItemViewType(int position) {
		final Cursor c = (Cursor) getItem(position);
		if (c == null) {
			return ITEM_TYPE_NONE;
		}
		final Status s = Status.parse(c);
		if (s == null || s.isNull()) {
			log("getItemViewType(" + position + ") is null.");
			return ITEM_TYPE_NONE;
		}
		if (s.text.contains("@" + App.me.userScreenName)) {
			return ITEM_TYPE_MENTION;
		} else {
			return ITEM_TYPE_NONE;
		}
	}

	@Override
	public int getViewTypeCount() {
		return viewTypes.length;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	public static final String TAG = "StatusCursorAdapter";

	private void log(String message) {
		Log.e(TAG, message);
	}

	public StatusCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	public StatusCursorAdapter(boolean colored, Context context, Cursor c) {
		super(context, c, false);
		this.colored = colored;
	}

	public StatusCursorAdapter(Activity context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	private void setTextStyle(ViewHolder holder) {
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.metaText.setTextSize(fontSize - 4);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setHeadImage(holder.headIcon);
		setTextStyle(holder);
		view.setTag(holder);
		bindView(view, context, cursor);
		return view;
	}

	@Override
	public void bindView(View view, Context context, final Cursor cursor) {
		View row = view;
		final ViewHolder holder = (ViewHolder) row.getTag();

		Status s = Status.parse(cursor);

		if (colored
				&& getItemViewType(cursor.getPosition()) == ITEM_TYPE_MENTION) {
			row.setBackgroundColor(0x33999999);
		}

		mLoader.setHeadImage(s.userProfileImageUrl, holder.headIcon);
		
		if (StringHelper.isEmpty(s.inReplyToStatusId)) {
			holder.replyIcon.setVisibility(View.GONE);
		} else {
			holder.replyIcon.setVisibility(View.VISIBLE);
		}

		if (StringHelper.isEmpty(s.photoLargeUrl)) {
			holder.photoIcon.setVisibility(View.GONE);
		} else {
			holder.photoIcon.setVisibility(View.VISIBLE);
		}

		holder.nameText.setText(s.userScreenName);
		StatusHelper.setSimpifiedText(holder.contentText, s.text);
		holder.metaText.setText(DateTimeHelper.getInterval(s.createdAt) + " 来自"
				+ s.source);

	}

	static class ViewHolder {
		ImageView headIcon = null;
		ImageView replyIcon = null;
		ImageView photoIcon = null;
		TextView nameText = null;
		TextView metaText = null;
		TextView contentText = null;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base
					.findViewById(R.id.item_status_head);
			this.replyIcon = (ImageView) base
					.findViewById(R.id.item_status_icon_reply);
			this.photoIcon = (ImageView) base
					.findViewById(R.id.item_status_icon_photo);
			this.contentText = (TextView) base
					.findViewById(R.id.item_status_text);
			this.metaText = (TextView) base.findViewById(R.id.item_status_meta);
			this.nameText = (TextView) base.findViewById(R.id.item_status_user);

		}
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_status;
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		return null;
	}

}