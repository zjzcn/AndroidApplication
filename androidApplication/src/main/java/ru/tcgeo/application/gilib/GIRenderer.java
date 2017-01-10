package ru.tcgeo.application.gilib;

import android.graphics.Bitmap;
import android.graphics.Rect;

import ru.tcgeo.application.gilib.models.GIBounds;
import ru.tcgeo.application.gilib.models.GIStyle;
import ru.tcgeo.application.gilib.models.Tile;
import rx.Observable;

public abstract class GIRenderer
{
	public abstract void RenderImage (GILayer layer, GIBounds area, int opacity, Bitmap bitmap, double scale);
	public abstract void RenderText (GILayer layer, GIBounds area, Bitmap bitmap, double scale);
	public abstract void RenderText (GILayer layer, GIBounds area, Bitmap bitmap, float scale_factor, double scale);
	abstract  public Observable<Tile> getTiles(GILayer layer, GIBounds area, Rect rect);
	public abstract void AddStyle (GIStyle style);
	public abstract int getType(GILayer layer);
}
