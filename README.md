android-parallax-recyclerview
============================

**Integration**
====

**Step 1.** Add the JitPack repository to your build file

   	repositories {
   	    maven {
   	        url "https://jitpack.io"
   	    }
   	}
   	
**Step 2.** Add the dependency

    dependencies {
    	compile 'com.github.kanytu:android-parallax-recyclerview:-SNAPSHOT'
    }

**USAGE**

(There is an example in `example` folder and in https://github.com/kanytu/example-parallaxrecycler)

 - Create a ViewHolder for your items

 ```java
 public class ExampleItemViewHolder extends RecyclerView.ViewHolder {
     public TextView textView;

     public ExampleItemViewHolder(View itemView) {
         super(itemView);
         textView = (TextView) itemView.findViewById(R.id.textView);
     }
 }
 ```

 - Create a subclass of `ParallaxRecyclerAdapter` with the desired content

```java
public class ExampleParallaxRecyclerAdapter extends ParallaxRecyclerAdapter<ExampleItemViewHolder> {
    private List<String> mContent;
    private Context mContext;

    public ExampleParallaxRecyclerAdapter(List<String> content, View header, RecyclerView view, boolean shouldClipView, Context context) {
        super(header, view, shouldClipView);
        mContent = content;
        mContext = context;
    }

    @Override
    public ExampleItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
        final ExampleItemViewHolder holder = new ExampleItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_recyclerview, parent, false));
        //don't set listeners on onBindViewHolder. For more info check http://androidshenanigans.blogspot.pt/2015/02/viewholder-pattern-common-mistakes.html
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, holder.textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindItemViewHolder(ExampleItemViewHolder holder, int position) {
        holder.textView.setText(mContent.get(position));
    }

    @Override
    public int getItemCountWithoutHeader() {
        return mContent.size();
    }
}
```

 - Create your object list and pass it to the constructor of `ParallaxRecyclerAdapter`

```java
List<String> myContent = new ArrayList<String>(); // example content
View header = LayoutInflater.from(this).inflate(R.layout.myParallaxView, myRecycler, false);
ExampleParallaxRecyclerAdapter myAdapter = new ExampleParallaxRecyclerAdapter(myContent, header, recyclerView, false, getContext());
```

 - Parallax scroll listener

```java
public class ExampleParallaxRecyclerAdapter extends ParallaxRecyclerAdapter<ExampleItemViewHolder> {

    // ...

    @Override
    public void onParallaxScroll(float percentage, float offset, View parallax) {
        // ...
    }

    // ...

}
```

**RESULT**

![ParallaxListView](https://raw.githubusercontent.com/kanytu/android-parallax-recycleview/master/screenshots/screenshot.gif)

**COOL EFFECTS YOU CAN DO WITH THIS LIBRARY**

 - Transparent toolbar effect

```java
public class ExampleParallaxRecyclerAdapter extends ParallaxRecyclerAdapter<ExampleItemViewHolder> {

    // ...

    @Override
    public void onParallaxScroll(float percentage, float offset, View parallax) {
        Drawable drawable = mToolbar.getBackground();
        drawable.setAlpha(Math.round(percentage * 255));
        mToolbar.setBackground(drawable);
    }

    // ...

}
```

![ParallaxListView](https://raw.githubusercontent.com/kanytu/android-parallax-recycleview/master/screenshots/parallaxtoolbar.gif)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--parallax--recyclerview-brightgreen.svg?style=flat)](https://android-arsenal.com/details/3/1095)

## License
Copyright (c) 2015 Pedro Oliveira

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
