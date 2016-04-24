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
The minimum configuration includes:
```
// creating GridLayoutManager and assigning it to RecyclerView
 DisplayMetrics dm = getResources().getDisplayMetrics();
 GridLayoutManager gridLayoutManager = new GridLayoutManager(dm);
 mRecyclerView.setLayoutManager(gridLayoutManager);

 // creating an instance of DataSource<I> class
 // and adding your DataProvider1D (or DataProvider2D) implementation
 DataSource<I> dataSource = new DataSource<>();
 dataSource.setDataProvider(yourDataProvider);
 gridLayoutManager.setDataSource(dataSource);

 // don't forget to keep DataSource data and
 // your RecyclerView's adapter data in sync!
 ```
