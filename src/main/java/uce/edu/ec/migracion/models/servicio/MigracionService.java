package uce.edu.ec.migracion.models.servicio;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uce.edu.ec.migracion.models.entity.*;
import uce.edu.ec.migracion.models.repository.IDatosDevengamientoRepository;
import uce.edu.ec.migracion.models.repository.IDocenteRepository;
import uce.edu.ec.migracion.models.repository.IRoleRepository;
import uce.edu.ec.migracion.models.repository.IUsuarioRepository;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MigracionService implements IMigracionService{

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private IDocenteRepository docenteRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IDatosDevengamientoRepository devengamientoRepository;

    @Override
    public void migrar() {
        Role role = new Role();
        role.setNombreRol("ROLE_USER");
        role = roleRepository.save(role);
        try (Connection conexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","DOCTORADO_PRODUCCION","YPS2020");) {
            File f = new File("C:\\Workspace\\Practicas\\Programs\\DevengamientoMicroServiceSpringboot\\migracion\\src\\main\\resources\\1.xlsx");
            List<String> docentesCedula = new ArrayList<>();
            InputStream inp = new FileInputStream(f);
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);
            int iRow = 1;
            Row row = sheet.getRow(iRow);
            while (row != null) {
                Cell cell = row.getCell(5);
                docentesCedula.add(String.valueOf(cell));
                iRow++;
                row = sheet.getRow(iRow);
            }
            ResultSet rsDocentes;
            Class.forName("oracle.jdbc.driver.OracleDriver");
            rsDocentes = conexion.prepareStatement("SELECT P.PRS_IDENTIFICACION, P.PRS_NOMBRES, P.PRS_PRIMER_APELLIDO, P.PRS_SEGUNDO_APELLIDO, D.DCN_FACULTAD FROM PERSONA P, DOCENTE D WHERE P.PRS_ID = D.PRS_ID").executeQuery();
            while (rsDocentes.next()) {
                for (String cedula : docentesCedula) {
                    if (rsDocentes.getString(1).equals(cedula)) {
                        Docente docente = new Docente();
                        docente.setCedula(rsDocentes.getString(1));
                        docente.setNombres(rsDocentes.getString(2));
                        docente.setApellidos(rsDocentes.getString(3) + " " + rsDocentes.getString(4));
                        docente = docenteRepository.save(docente);
                        Usuario usuario = new Usuario();
                        usuario.setUsername(rsDocentes.getString(1));
                        usuario.setPassword(passwordEncoder.encode(rsDocentes.getString(1)));
                        List<Role> roles = new ArrayList<>();
                        roles.add(role);
                        usuario.setRoles(roles);
                        usuarioRepository.save(usuario);
                        DatosDevengamiento devengamiento = new DatosDevengamiento();
                        devengamiento.setFacultad(rsDocentes.getString(5));
                        devengamiento.setIdDocente(docente);
                        devengamientoRepository.save(devengamiento);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
