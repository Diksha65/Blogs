package com.example.diksha.blogs;

/**
 * Created by diksha on 9/7/17.
 */
/*
abstract public class SingleFragmentRecyclerView extends Fragment{

    private RecyclerView recyclerView;
    private TextView emptyText;
    public static boolean isPagerOn = false;
    private static DataStash dataStash = DataStash.DATA_STASH;

    protected abstract void createRecyclerViewAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        isPagerOn = false;
        emptyText = (TextView)view.findViewById(R.id.emptyView);
        emptyText.setText("No blogs");
        showProgressDialog("Fetching Data");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        createRecyclerViewAdapter();

        return view;
    }

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(msg);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
*/