package org.neo4j.cypherdsl.querydsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QStuff is a Querydsl query type for Stuff
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStuff extends EntityPathBase<Stuff> {

    private static final long serialVersionUID = -906824655L;

    public static final QStuff stuff = new QStuff("stuff");

    public final StringPath name = createString("name");

    public QStuff(String variable) {
        super(Stuff.class, forVariable(variable));
    }

    public QStuff(Path<? extends Stuff> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStuff(PathMetadata<?> metadata) {
        super(Stuff.class, metadata);
    }

}

