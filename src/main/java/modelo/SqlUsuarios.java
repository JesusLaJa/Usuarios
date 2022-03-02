package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUsuarios extends Conexion {

    //se crean las variables 
    PreparedStatement ps = null;
    ResultSet rs = null;
    Connection con = conectar();

    public boolean registrar(usuarios usr) throws SQLException {

        //se llama la funcion INSERT para agregar los registros en la base de datos
        String sql = "INSERT  INTO usuarios (usuario, password, nombre, correo, id_tipo) VALUES(?,?,?,?,?)";
        //se prepara la coneccion con sql
        ps = con.prepareStatement(sql);
        //se agregan todos los valores que se necesitan para poder insertarlos
        ps.setString(1, usr.getUsuario());
        ps.setString(2, usr.getPassword());
        ps.setString(3, usr.getNombre());
        ps.setString(4, usr.getCorreo());
        ps.setInt(5, usr.getId_tipo());
        //se ejecuta
        ps.execute();
        //se retorna true en caso de que haya funcionado correctamente
        return true;
    }

    public int usuarioExistente(String usuario) throws SQLException {

        //se llama la dunción SELECT y count para contar los ID de la tabla usuarios
        String sql = "SELECT count(id) FROM usuarios WHERE usuario = ?";

        //se prepara la coneccion con sql
        ps = con.prepareStatement(sql);
        //se agrega el valor que se va a seleccionar
        ps.setString(1, usuario);
        //se le asigna un valor a ResultSet
        rs = ps.executeQuery();

        //se recorre el conjunto de resultados de la consulta
        if (rs.next()) {

            //se retorna el ID
            return rs.getInt(1);
        }
        return 1;
    }

    public boolean validarEmail(String correo) {

        //Pattern va a funcionar para validar el patrón de el mail
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\"
                + ".[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        //se encarga de hacer la validación del String con el patrón que le estamos pidiendo 
        Matcher matcher = pattern.matcher(correo);

        //nos devuelve el patrón en caso de que si haya sido correcto
        return matcher.find();
    }

    public boolean login(usuarios usr) throws SQLException {

        //se llama la función SELECT y count para contar los ID de la tabla usuarios
        String sql = "SELECT id, usuario, password, nombre, id_tipo FROM usuarios WHERE usuario = ?";

        //se prepara la coneccion con sql
        ps = con.prepareStatement(sql);
        //se agrega el valor que se va a seleccionar
        ps.setString(1, usr.getUsuario());
        //se le asigna un valor a ResultSet
        rs = ps.executeQuery();

        //se recorre el conjunto de resultados de la consulta
        if (rs.next()) {

            //cuando la contraseña sea igual a la cnontraseña del usuariio
            //ingresado se ejecutará el IF
            if (usr.getPassword().equals(rs.getString(3))) {

                //llamo a la función sql UPDATE y actualizo la fecha de ingreso
                String sqlUpdate = "UPDATE usuarios SET last_session = ? WHERE id = ?";

                //se prepara la coneccion con la declaración sql
                ps = con.prepareStatement(sqlUpdate);
                ps.setString(1, usr.getLast_session());
                ps.setInt(2, rs.getInt(1));
                ps.execute();

                usr.setId(rs.getInt(1));
                usr.setNombre(rs.getString(4));
                usr.setId_tipo(rs.getInt(5));
                return true;

            } else {
                return false;
            }
        }
        return false;
    }
}
