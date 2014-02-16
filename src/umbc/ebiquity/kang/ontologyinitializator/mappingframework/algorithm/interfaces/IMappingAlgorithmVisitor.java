package umbc.ebiquity.kang.ontologyinitializator.mappingframework.algorithm.interfaces;


public interface IMappingAlgorithmVisitor {

	public void visit(IRelation2PropertyMappingAlgorithm algorithm);

	public void visit(IInstanceClassificationAlgorithm algorithm);
}
