# NgModule和组件
NgModule为其中的组件提供了一个编译上下文环境。根模块总会有一个根组件，并在引导期间创建它。但是，任何模块都能包含任意数量的其他组件。

@Component的配置选项：
- Selector:是一个CSS选择器，它会告诉Angular,一旦在模板HTML中找到了这个选择器对应的标签，就创建并插入该组件的一个实例。比如，如果应用的HTML中包含<app-hero-list></app-hero-list>，Angular就会在这些标签中插入一个HeroListComponent实例的视图。
- templateUrl:该组件的HTML模板文件相对于这个组件文件的地址。另外，你还可以用template属性的值内联的HTML模板。这个模板定义了该组件的宿主视图。
- providers:当前组件所需的服务提供者的一个数组。在这个例子中，它告诉Angular该如何提供一个HeroService实例，以获取要显示的英雄列表。

## 模板语法
模板很像标准的HTML，但是它还包含了Angular的模板语法，这些模板语法可以根据你的应用逻辑，应用状态和DOM数据来修改这些HTML。你的模板可以使用数据绑定来协调应用和DOM中的数据，使用管道在显示出来之前对其进行转换，使用指令来把程序逻辑应用到要显示的内容上。

## 数据绑定
Angular支持双向数据绑定，这是一种对模板中的各个部件与组件中的各个部件进行协调的机制。往模板HTML中添加绑定标记可以告诉Angular该如何连接他们。

- {{value}}:表示Component绑定到DOM上, Component -> DOM
- [property]="value",表示Component绑定到DOM上, Component -> DOM
- (event)="handler",表示从DOM绑定到Component, DOM -> Component
- [(ng-model)] = "property", 表示DOM和Component双向绑定， DOM <-> Component

```JavaScript
<li>{{hero.name}}</li>
<app-hero-detail [hero]="selectedHero"></app-hero-detail>
<li (click)="selectHero(hero)"></li>
```

双向数据绑定，主要用于模板驱动表单中，它会把属性绑定和事件绑定组合成一种单独的写法。
```javaScript
<input [(ngmodel)] = "hero.name">
```
在双向数据绑定中，数据属性值通过属性绑定从组件流到输入框，用户的修改通过事件绑定流回组件，把属性值设置为最新的值。

## 管道
Angular的管道可以让你在模板中声明显示值的转换逻辑。要在HTML模板中指定值的转换方式，使用管道操作符（|），例如： {{interpolated_value | pipe_name}}

<p>Today is {{today | date}}</p>

## 指令
Angular的模板是动态的，当Angular渲染他们的时候，会根据指令给出的指示对DOM进行转换。指令就是一个带有@Directive()装饰器的类。

组件从技术角度上来说就是一个指令，但是由于组件对 Angular 应用来说非常独特、非常重要，因此 Angular 专门定义了 @Component() 装饰器，它使用一些面向模板的特性扩展了 @Directive() 装饰器。

点击进入@Component注解，可以看到以下内容，可以说明确实应该是@Component继承自@Directive
```java
/**
 * Type of the Directive metadata.
 */
export declare const Directive: DirectiveDecorator;
/**
 * Component decorator interface
 *
 */
export interface ComponentDecorator {
```

### 结构型指令
除组件外，还有两种指令：结构型指令和属性型指令。Angular本身定义了一系列这两种类型的指令，也可以使用@Directive（）装饰器来定义自己的指令。

结构型指令通过添加、移除或替换DOM元素来修改布局。

```java
<li *ngFor="let hero of heroes"></li>
<app-hero-detail *ngIf="selectedHero"></app-hero-detail>
```
- *ngFor 是一个迭代器，它要求 Angular 为 heroes 列表中的每个英雄渲染出一个 <li>。
- *ngIf 是个条件语句，只有当选中的英雄存在时，它才会包含 HeroDetail 组件。
这种写法和Mybatis中的动态SQL写法很类似。
  
### 属性型指令
属性型指令会修改现有元素的外观或行为。在模板中，他们看起来就像普通的HTML属性一样，因此得名“属性型指令”

ngModel指令就是属性型指令的一个例子，它实现了双向数据绑定。
