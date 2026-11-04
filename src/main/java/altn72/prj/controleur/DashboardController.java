package altn72.prj.controleur;

import altn72.prj.dto.DashboardDto;
import altn72.prj.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardDto obtenirTableauDeBord() {
        return dashboardService.obtenirTableauDeBord();
    }
}