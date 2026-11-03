package altn72.prj.modele;

public enum TypeProfil {
    RESPONSABLE_EXPLOITATION("Responsable d'exploitation"),
    AGENT("Agent");

    private final String libelle;

    TypeProfil(String libelle) { this.libelle = libelle; }

    public String getLibelle() { return libelle; }
}
