# Grid Layout Manager #

Grid Layout Manager is an implementation of RecyclerView.LayoutManager class
that allows RecyclerView to display views in two dimensional grid.

![Screenshot horizontal](https://github.com/slesinskik/grid-layout-manager/raw/develop/wiki/orientation_horizontal.png)
![Screenshot vertical](https://github.com/slesinskik/grid-layout-manager/raw/develop/wiki/orientation_vertical.png)

An example, real-life use, can be a TV Guide:

![Screenshot TV Guide](https://github.com/slesinskik/grid-layout-manager/raw/develop/wiki/tv_guide_example.png)


## Using Grid Layout Manager ##

### Configuration ###

The easiest way to get started is by including the following in
your project's `build.gradle` file:

```gradle
compile 'com.kamilslesinski.gridlayoutmanager:gridlayoutmanager:X.Y.Z'
```

### Usage ###

A sample app is provided in `sample-app` folder.
There are just a few basic steps necessary to be up and running:

* creating GridLayoutManager and assigning it to your RecyclerView
```
DisplayMetrics dm = getResources().getDisplayMetrics();
GridLayoutManager gridLayoutManager = new GridLayoutManager(dm);
mRecyclerView.setLayoutManager(gridLayoutManager);
```

* optimizing RecyclerViewPool size

This is almost always a must. Default implementation of RecycledViewPool stores only 5 scraped views,
which is fine for a LinearLayoutManager which usually adds or removes 1 view at a time.
However, due to how items are laid out using GridLayoutManager, a lot of views can be added/removed
 at the same time when scrolling (i.e. removing or adding a whole row of views when scrolling vertically).
To avoid excessive creation and garbage collection of views when scrolling, we should
increase RecycledViewPool size.

It should be greater than the count of strips and greater than average count of visible items per strip.

```
RecyclerView.RecycledViewPool rvc = new RecyclerView.RecycledViewPool();
rvc.setMaxRecycledViews(0, POOL_SIZE);
mRecyclerView.setRecycledViewPool(rvc);
```

* creating an instance of DataSource<I> with your DataProvider1D (or DataProvider2D) implementation
```
DataSource<I> dataSource = new DataSource<>();
dataSource.setDataProvider(yourDataProvider);
```

* passing DataSource object to your GridLayoutManager
```
gridLayoutManager.setDataSource(dataSource);
// this is also a good time to make sure that
// your RecyclerView adapter data is in sync with `dataSource` object
```

### Documentation ###

GridLayoutManager allows you to customize layout by providing orientation (vertical / horizontal) and
size of strips:
```
setOrientation(@Orientation int orientation) {
setStripSize(int sizeInDp);
setStripLength(int sizeInDp);
```
Please check provided javadoc for additional information.