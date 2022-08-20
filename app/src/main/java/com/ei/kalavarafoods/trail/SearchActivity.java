package com.ei.kalavarafoods.trail;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.api.SearchResult;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.ui.search.SearchViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.recyclerView2)
    RecyclerView recyclerView;

    private SearchViewModel mSearchViewModel;
    private ApiInterface mApiInterface;
    private List<Product> products;
    private Disposable disposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
//        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fromView(searchView)
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !s.isEmpty();
                    }
                })
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<SearchResult>>() {
                    @Override
                    public ObservableSource<SearchResult> apply(String s) throws Exception {
                        return mSearchViewModel.rxSearchProducts(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(SearchResult searchResult) {
                        products = searchResult.getProduct();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        recyclerView.setAdapter(new RvAdaptr(products));
                    }
                });
    }


    public static Observable<String> fromView(SearchView searchView){
        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                subject.onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                subject.onNext(s);
                return true;
            }
        });

        return subject;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    class RvAdaptr extends RecyclerView.Adapter<RvAdaptr.ViewHold>{
        private List<Product> products;

        RvAdaptr(List<Product> products){
            this.products = products;
        }

        @Override
        public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_brand_names, parent, false);

            return new ViewHold(v);
        }

        @Override
        public void onBindViewHolder(ViewHold holder, int position) {
            holder.bindData(products.get(position).getProductName());
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        public class ViewHold extends RecyclerView.ViewHolder {

            @BindView(R.id.tvBrandName)
            TextView tvBrandName;

            public ViewHold(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(String name){
                tvBrandName.setText(name);
            }
        }
    }

}
