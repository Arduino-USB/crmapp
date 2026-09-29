package com.crm.entites;

import com.crm.webscrape.WebReturns;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "insurance_files") 
public class Insurance {

    public enum InsuranceStatus {
        POSTPROCESS,
        UPLOAD,
        COMPLETE
    }

    @Id
    @GeneratedValue
    private Long id;

    private Instant lastUpdated;
    private Instant lastSyncedWithDentrix;    
    
    @Column(columnDefinition = "BYTEA")
    private byte[] pdfFile;

    @Embedded
    private WebReturns rawInsuranceData;

    @Enumerated(EnumType.STRING)
    private InsuranceStatus status = InsuranceStatus.COMPLETE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Instant getLastSyncedWithDentrix() {
        return lastSyncedWithDentrix;
    }

    public void setLastSyncedWithDentrixNow() {
        this.lastSyncedWithDentrix = Instant.now();
    }

    public WebReturns getRawInsuranceData() {
        return rawInsuranceData;
    }

    public void setRawInsuranceData(WebReturns rawInsuranceData) {
        this.rawInsuranceData = rawInsuranceData;
        this.lastUpdated = Instant.now();
        this.status = InsuranceStatus.POSTPROCESS; // Needs PDF generation
    }

    public byte[] getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(byte[] pdfFile) {
        this.pdfFile = pdfFile;
        this.status = InsuranceStatus.UPLOAD; // Ready for Dentrix upload
    }

    public InsuranceStatus getStatus() {
        return status;
    }

    public void setStatus(InsuranceStatus status) {
        this.status = status;
    }

    public void markAsComplete() {
        setLastSyncedWithDentrixNow();
        this.status = InsuranceStatus.COMPLETE; // Fully finished
    }
}
