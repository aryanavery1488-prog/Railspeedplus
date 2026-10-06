# Railspeedplus
# RailPlus

railplus is a paper plugin that makes minecarts faster.

thats basically it.

it doesnt add custom rails or blocks or require a resource pack or anything on the player side. you just install it on the server and use the normal minecraft minecarts and rails.

the reason i made it this way is because i didnt want to replace the vanilla minecart system. i wanted to add onto it.

so powered rails still work, hills still work, turns still work, etc. if railplus isnt giving the cart a bonus then its still just a normal minecart.

you can also change pretty much all of the numbers in the config and turn each part on/off.

## what it actually does

- player riding the cart makes it faster
- gold, iron and redstone blocks can boost it
- naming a cart `fast`, `turbo` or `slow` changes its speed
- furnace minecarts are faster and keep fuel longer
- all of those things stack
- theres a max speed so you cant make a cart go infinitely fast
- config can be reloaded without restarting the server

## the speed thing

vanilla minecarts have a max speed of 0.4 blocks per tick.

railplus doesnt just change that to 1.5 or whatever you put in the config.

it takes the normal 0.4 and adds the bonuses.

so something like:

```text id="5e2w9k"
normal cart       0.40
player           +0.15
gold block       +0.50
fast name        +0.30
                  -----
                   1.35
```

would give the cart a max speed of 1.35.

the other thing is that the cart actually gets pushed toward that speed.

i didnt want it to just change some number and have the cart slowly do whatever vanilla was already going to do. when it enters a new block it gets a little push based on the bonus.

so if it goes onto a gold block it starts speeding up.

when it leaves the gold block that bonus goes away.

negative bonuses work too, so you can use them to slow a cart down.

## player boost

if a player is actually sitting in the cart, it gets the rider bonus.

empty carts dont get it and carts with mobs dont get it either.

default is +0.15.

```yaml id="w8o2l7"
rider: true
rider-speed: 0.15
```

dont want it?

```yaml id="f1p4a6"
rider: false
```

done.

## block boosts

the plugin checks the block directly underneath the rail.

default values are:

```text id="q9n3ds"
iron       +0.35
gold       +0.50
redstone   +0.60
```

so you can basically make boost pads out of blocks you already have.

you could have a normal railway and then put gold blocks under one section to make that part faster.

or use redstone blocks for the really fast parts.

config:

```yaml id="7v4m2x"
blocks: true
gold-block: 0.5
iron-block: 0.35
redstone-block: 0.6
```

the boost only exists while the cart is over that block.

## name tags

this is probably one of the simpler parts.

rename a minecart with a name tag and railplus checks the name.

its not case sensitive.

```text id="2c6j8r"
fast     +0.30
turbo    +0.80
slow     -0.20
```

`fast` is just a faster cart.

`turbo` is a lot faster.

`slow` actually lowers the speed so you can have carts that dont fly through a station.

config:

```yaml id="n5r1yu"
names: true
name-fast: 0.3
name-turbo: 0.8
name-slow: -0.2
```

and yes, these stack with the other stuff.

so a player riding a turbo cart over a redstone block is gonna be pretty damn fast.

thats why theres a max speed.

## furnace minecarts

furnace minecarts are also changed because honestly vanilla furnace carts dont get used much.

while the furnace has fuel, railplus gives it +0.25 speed by default.

it also makes the fuel last longer.

default is 3x.

so:

```text id="e6s2p9"
1 = normal
3 = 3 times as long
5 = 5 times as long
```

config:

```yaml id="k3d7v1"
furnace: true
furnace-speed: 0.25
furnace-fuel: 3
```

once the cart runs out of fuel the extra speed goes away.

it can still have the other bonuses though.

## max speed

if you let every bonus stack without a limit you can get some really stupid speeds.

default:

```yaml id="b2x8m4"
max-speed: 1.5
```

thats around 30 blocks per second.

the cart cant go above that even if its bonuses would put it higher.

you can raise it if you want. just test it first because really fast minecarts can get weird around turns and unloaded chunks.

## performance

i didnt want this thing running some giant check every tick for every minecart.

most of the work happens when a cart actually enters a new block.

if its still in the same block theres not really anything to recalculate.

it also doesnt keep setting the cart speed if nothing changed.

furnace fuel is checked once a second.

theres no database and its not constantly reading the config or searching the whole world for minecarts.

basically its supposed to just sit there and do its thing without adding a bunch of useless server load.

## config

everything is in:

`plugins/RailPlus/config.yml`

```yaml id="r6c1t8"
max-speed: 1.5

rider: true
rider-speed: 0.15

blocks: true
gold-block: 0.5
iron-block: 0.35
redstone-block: 0.6

names: true
name-fast: 0.3
name-turbo: 0.8
name-slow: -0.2

furnace: true
furnace-speed: 0.25
furnace-fuel: 3
```

you can turn stuff off completely with the true/false settings.

or just set one of the numbers to 0 if you dont want that specific bonus.

after changing it:

```text id="u7p3k5"
/railplus reload
```

you dont have to restart the server.

## command

`/railplus reload`

permission:

`railplus.admin`

ops have it by default.

## installing it

put `RailPlus.jar` into your `plugins` folder.

start the server.

change the config if you want.

run `/railplus reload`.

thats pretty much it.

### requirements

- paper or a paper based fork
- minecraft 1.21+
- java 21

players dont need anything.

## what id actually use it for

probably the biggest thing is transportation.

you could have normal minecarts around a city, faster ones for long distance travel, boost sections on a subway, furnace carts for cargo, or just make a minecart racing track.

you dont have to use everything either.

if all you want is gold blocks making carts faster, you can turn everything else off and just use that.

## note

this is still new and i havent tested it on every single paper setup.

id definitely test it before putting it on a live server if youre using really high speeds or a huge rail network.

if something breaks or a cart starts doing some weird shit its not supposed to do, let me know.
