
MrBean
======

Let MrBean generate most of the boiler plate for you!

![logo-teddy.jpg](logo-teddy.jpg)

Based on the open source plugin: [Guava Generators](https://github.com/seanlandsman/guavagenerators)

# Install

1. Download the [plugin binary](mrbean.zip)
2. Go to: `File > Settings... > Plugins > Install plugin from disk...` and select `mrbean.zip`

# Usage

Execute the `Code > Generate...` action (`alt + insert`) and select the method(s) that you want to
generate.

# Configuration

To change the templates, open the plugin settings panel: `File > Settings... > Other Settings > 
MrBean`. There you can edit them to suit your needs.

If you want to reset a template, just press the big ugly `Default` button at its right.

# Some ideas (TODO)

* Add tests
* Port to Kotlin
* Rearrange methods in class after generating code
* Add support for 'EnumSet' in 'with' and 'set'
* Add 'toString' version to avoid 'nulls' like:

    Stream.of (
      new AbstractMap.SimpleEntry<> ("field1", field1),
      new AbstractMap.SimpleEntry<> ("field2", field2)
    )
    .filter (e -> e.getValue () != null)
    .map (e -> e.getKey () + ": " + e.getValue ())
    .collect (joining (", ", this.getClass().getSimpleName() + " {", "}"))

* Add comparator, constructor and all together.
* Add actions dinamically depending on templates in settings. Check [the action system].
* Change settings for a table that opens method details when one is selected.
* Refactor the '##~' hack to have multiple methods per generator.
* Call other generators. Ie: constructor with parameters.
* Use code editors to change the templates.

[the action system]: https://www.jetbrains.com/idea/plugins/action_system.html

