package claudetsiang.fr.interfaces;

public interface RecyclerViewClickListener {
    void onItemClick(int position);

    void onLongItemClick(int position);

    void onViewButtonClick(int position);
    void onAddButtonClick(int position);
    void onEditButtonClick(int position);
    void onDeleteButtonClick(int position);
}
