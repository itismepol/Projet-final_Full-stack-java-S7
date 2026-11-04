package altn72.prj.dto;

import java.util.List;

public record DashboardDto(
        List<DashboardIncidentDto> incidents,
        long totalActifs,
        long enAttenteAffectation,
        long affectes,
        long urgents,
        long prioriteCritique
) {}
