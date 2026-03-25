package Gioi4;

import java.util.ArrayList;
import java.util.List;

public class PatientDTO {
    private int patientId;
    private String patientName;
    private List<PrescriptionDTO> dsDichVu = new ArrayList<>();

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public List<PrescriptionDTO> getDsDichVu() {
        return dsDichVu;
    }

    public void setDsDichVu(List<PrescriptionDTO> dsDichVu) {
        this.dsDichVu = dsDichVu;
    }
}
