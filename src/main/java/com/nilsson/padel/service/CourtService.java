package com.nilsson.padel.service;

import com.groupc.shared.exception.ResourceNotFoundException;
import com.nilsson.padel.dto.CourtRecord;
import com.nilsson.padel.entity.Court;
import com.nilsson.padel.repository.CourtRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ──────────────────────────────────────────────
 * <h2>CourtService</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Hanterar affärslogik för padelbanor, inklusive registrering, uppdatering, hämtning och borttagning.</p>
 * <p><strong>Funktioner:</strong></p>
 * <ul>
 * <li>Hämtar en eller flera padelbanor</li>
 * <li>Skapar och uppdaterar baninformation</li>
 * <li>Raderar befintliga banor</li>
 * <li>Validerar att efterfrågad bana existerar</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> Spring {@code @Service} som använder repository-lagret och mappar mellan entitet och {@code CourtRecord}.</p>
 * ──────────────────────────────────────────────
 */
@Service
public class CourtService {

    private final CourtRepository courtRepository;

    private final static Logger logger = LoggerFactory.getLogger(CourtService.class);

    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    public List<CourtRecord> getAllCourts() {
        logger.info("Hämtar alla banor:");
        return courtRepository.findAll().stream()
                .map(this::mapToRecord)
                .toList();
    }

    public CourtRecord getCourtById(Long id) {
        logger.info("Hämtar bana med ID: {}", id);
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Padelbana med ID " + id + " hittades inte i systemet."));

        return mapToRecord(court);
    }

    public CourtRecord createCourt(CourtRecord request) {
        logger.info("Skapar ny bana: {}", request.name());

        Court newCourt = new Court(
                request.name(),
                request.description(),
                request.isIndoor(),
                request.pricePerHourSek()
        );

        Court savedCourt = courtRepository.save(newCourt);

        logger.info("Padelbana {} skapad framgångsrikt.", savedCourt.getName());
        return mapToRecord(savedCourt);
    }

    public CourtRecord updateCourt(Long id, CourtRecord request) {
        logger.info("Uppdaterar bana med ID: {}", id);

        Court existingCourt = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kunde inte uppdatera: Padelbana med ID " + id + " hittades inte."));

        existingCourt.setName(request.name());
        existingCourt.setDescription(request.description());
        existingCourt.setIndoor(request.isIndoor());
        existingCourt.setPrice(request.pricePerHourSek());

        Court updatedCourt = courtRepository.save(existingCourt);

        logger.info("Padelbana {} uppdaterad framgångsrikt.", updatedCourt.getName());
        return mapToRecord(updatedCourt);
    }

    public void deleteCourt(Long id) {
        logger.warn("Raderar bana med ID: {}", id);

        if (!courtRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kunde inte radera: Padelbana med ID " + id + " hittades inte.");
        }
        courtRepository.deleteById(id);
        logger.info("Padelbana {} raderad framgångsrikt.", id);
    }

    private CourtRecord mapToRecord(Court court) {
        return new CourtRecord(
                court.getId(),
                court.getName(),
                court.getDescription(),
                court.isIndoor(),
                court.getPrice()
        );
    }
}
