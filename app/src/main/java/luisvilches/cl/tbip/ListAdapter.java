package luisvilches.cl.tbip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vilches on 28-07-16.
 */
public class ListAdapter<T> extends ArrayAdapter<Tarjeta> {
    private final List<Tarjeta> list;
    private final Context context;

    public ListAdapter(Context context, List<Tarjeta> list) {
        super(context, R.layout.itemlist);
        this.context = context;
        this.list = list;

    }

    static class ViewHolder {
        protected TextView textNombre;
        protected TextView textNumero;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        LayoutInflater inflator = LayoutInflater.from(context);
        view = inflator.inflate(R.layout.itemlist,null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.textNombre = (TextView) view.findViewById(R.id.textNombre);
        viewHolder.textNumero = (TextView) view.findViewById(R.id.textNumero);
        view.setTag(viewHolder);

        viewHolder.textNombre.setText(list.get(position).getNombreTarjeta());
        viewHolder.textNumero.setText(list.get(position).getNumeroTarjeta());

        return view;
    }
}
