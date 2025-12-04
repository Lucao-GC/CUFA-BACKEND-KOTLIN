package cufa.conecta.application.dto.response.usuario

class CandidaturaFilaDto(
    private var nomeCandidato: String?,
    private var tituloVaga: String?
) {

    fun getNomeCandidato(): String? {
        return nomeCandidato
    }

    fun setNomeCandidato(nomeCandidato: String) {
        this.nomeCandidato = nomeCandidato
    }

    fun getTituloVaga(): String? {
        return tituloVaga
    }

    fun setTituloVaga(tituloVaga: String) {
        this.tituloVaga = tituloVaga
    }
}
