package org.neo4j.cypherdsl.querydsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QPlace is a Querydsl query type for Place
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPlace extends EntityPathBase<Place> {

    private static final long serialVersionUID = -909852860L;

    public static final QPlace place = new QPlace("place");

    public final StringPath name = createString("name");

    public QPlace(String variable) {
        super(Place.class, forVariable(variable));
    }

    public QPlace(Path<? extends Place> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlace(PathMetadata<?> metadata) {
        super(Place.class, metadata);
    }

}

