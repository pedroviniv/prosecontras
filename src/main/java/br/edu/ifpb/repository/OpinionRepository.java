package br.edu.ifpb.repository;

import br.edu.ifpb.entity.Opinion;
import br.edu.ifpb.entity.Topic;

import java.util.List;

/**
 * Created by kieckegard on 02/09/2016.
 */
public interface OpinionRepository {

    void save(Opinion opinion);
    List<Opinion> listByTopic(Topic topic);

}
