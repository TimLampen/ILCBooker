/**
 * Created by Timothy Lampen on 10/28/2019.
 */
public enum Room{
    BMH319("BMH 319",40),
    BMH229("BMH 229",26),
    BMH228("BMH 228",25),
    BMH220("BMH 220",19),
    BMH218("BMH 218",17),
    BMH321("BMH 321", 32);

    int id;
    String formattedName;

    Room(String formattedName, int id){
        this.id = id;
        this.formattedName = formattedName;
    }

    public int getId() {
        return id;
    }

    public String getFormattedName() {
        return formattedName;
    }
}
