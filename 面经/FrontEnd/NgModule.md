# NgModule和组件
NgModule为其中的组件提供了一个编译上下文环境。根模块总会有一个根组件，并在引导期间创建它。但是，任何模块都能包含任意数量的其他组件。

@Component的配置选项：
- Selector:是一个CSS选择器，它会告诉Angular,一旦在模板HTML中找到了这个选择器对应的标签，就创建并插入该组件的一个实例。比如，如果应用的HTML中包含<app-hero-list></app-hero-list>，Angular就会在这些标签中插入一个HeroListComponent实例的视图。
- templateUrl:该组件的HTML模板文件相对于这个组件文件的地址。另外，你还可以用template属性的值内联的HTML模板。这个模板定义了该组件的宿主视图。
- providers:当前组件所需的服务提供者的一个数组。在这个例子中，它告诉Angular该如何提供一个HeroService实例，以获取要显示的英雄列表。

