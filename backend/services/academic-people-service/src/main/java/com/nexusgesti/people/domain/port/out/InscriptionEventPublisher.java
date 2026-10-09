package com.nexusgesti.people.domain.port.out;

import com.nexusgesti.people.domain.model.Eleve;
import com.nexusgesti.people.domain.model.Inscription;

public interface InscriptionEventPublisher {

    void publierEleveInscrit(Eleve eleve, Inscription inscription);
}
