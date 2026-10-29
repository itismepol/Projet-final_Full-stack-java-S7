package altn72.prj.service;

import altn72.prj.modele.Equipement;
import altn72.prj.modele.repository.EquipementRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IndicateurUrgenceService {

    private final EquipementRepository equipementRepository;

    public IndicateurUrgenceService(EquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }

    public Optional<Equipement> getEquipement(Long idProgrammeur) {

        return equipementRepository.findById(idProgrammeur);
    }

    // @Transactional ici aussi : on enchaîne 2 opérations (vérifier l'existence, puis supprimer) qui forment un tout
    @Transactional
    public boolean supprimerProgrammeur(Integer idProgrammeur) {
        // Ancienne version : void + exception si id inexistant (=> 500)
        // Maintenant on renvoie un booléen (true = supprimé, false = introuvable) : le contrôleur en déduit 204 ou 404
        // [Un échange hier (08/10/2026) en séance avec l'un de vous m'a conduit à faire cette modif. Merci ;-) ]
        // existsById() suffit : inutile de charger tout l'objet juste pour savoir s'il existe
        // Attention : depuis Spring Data JPA 3, deleteById() sur un id inexistant ne lève plus d'exception, il ne fait rien
        // Sans ce test, on ne pourrait donc pas répondre 404
        // 5.10.3 -- Le service ne connaît rien de la vue : il renvoie seulement true/false. C'est le contrôleur qui décide de rediriger vers la liste ou de signaler le 404.
        if (!programmeurRepository.existsById(idProgrammeur)) return false;
        programmeurRepository.deleteById(idProgrammeur);
        return true;
    }

    // Ancienne version : void. On renvoie maintenant le programmeur enregistré
    // Pourquoi ? save() renvoie l'objet avec l'id généré par la base : le contrôleur en a besoin pour construire
    // l'en-tête Location de la réponse 201 (l'URL de la nouvelle ressource)
    // 5.11 -- Avec Thymeleaf ce retour n'est plus exploité (le contrôleur redirige simplement vers la liste). On le garde tel quel :
    // la couche Service n'a pas à changer quand le type de client change (JSON ou page HTML) : c'est tout l'intérêt de la séparation en couches.
    public Programmeur ajouterProgrammeur(Programmeur programmeur) {
        // On force l'id à null : si le client envoyait un id existant dans le JSON, save() MODIFIERAIT ce programmeur
        // au lieu d'en créer un nouveau. Un POST doit toujours créer : l'id est généré par la base
        programmeur.setId(null);
        //programmeurRepository.save(programmeur);
        return programmeurRepository.save(programmeur);
    }

    // @Transactional : lecture + modification forment une seule unité de travail (tout ou rien)
    // Sous le capot : Spring ouvre une transaction avant la méthode, la valide (commit) à la fin, ou l'annule (rollback) si une RuntimeException est lancée
    @Transactional
    public Optional<Programmeur> modifierProgrammeur(Integer idProgrammeur, Programmeur programmeurModified) {
        // Ancienne version : void + orElseThrow() sans argument (NoSuchElementException => 500 si id inexistant).
        // Avec map(), on ne modifie que si le programmeur existe, sinon on renvoie un Optional vide (=> 404 dans le contrôleur).
        // 5.12.2 -- Les nouvelles données viennent maintenant d'un formulaire HTML (@ModelAttribute) et non plus d'un JSON (@RequestBody) : le service n'est pas modifié.
        return programmeurRepository.findById(idProgrammeur).map(programmeurToModify -> {
            BeanUtils.copyProperties(programmeurModified, programmeurToModify, "id");   // "id" ignoré : l'id du JSON ne doit jamais remplacer celui de l'URL
            //programmeurRepository.save(programmeurToModify);    // Impact de @Transactional. Pourquoi?
            // Réponse : dans la transaction, l'objet est "managed" par Hibernate. Au commit, il compare l'objet à son état d'origine
            // (dirty checking) et génère lui-même l'UPDATE. Le save() est donc inutile. Sans @Transactional : rien n'est enregistré en base.
            // Mais comme vu en séance le 08/10/2026 garder le save() conduit à une meilleure lisibilité et contribue grandement à éviter toute confusion ou ambiguité à la lecture du code
            return programmeurToModify;
        });
    }
}

