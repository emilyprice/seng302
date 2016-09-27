# Design Decisions

This is where we document all the major decisions made in the development process.

[Scales](#scales)

## Scales (How far do we go?)<a name="scales"></a>

The decision has been made to only accept scales with one sharp or flat in the name.
For example: E# major is fine, but Fbb major is not.

The main reason for this is that some scales that have double flats or double sharps in
the name will require triple sharps or triple flats as output. This is not something that 
our program will accept.

Details of the correspondence can be seen below:
>Hi Moffat,
>
>I have a question related to music theory (sorry!) and the scale command.
>
>In general, for other commands we can accept any enharmonic up to double sharps and double flats - so semitone up Ebb is totally fine. However scales are slightly different. If we accept double sharps or double flats  (e.g scale Ebb major) then some results will need triple sharps or triple flats. This doesn't really make sense in music theory.
>
>Usually in music theory only scales in the 'circle of fifths' are used, however in theory the circle is really an infinite spiral. There are some exceptions where other theoretical scales may be used, such as G# major.
>
>My suggestion to deal with this is to not accept any double flats or double sharps as input for the scale command. An alternative could be to return the base enharmonic scale (so Ebb major would give D major) but I feel like this doesn't make sense because even though they sound the same, they are not the same scale.
>
>Let me know what you think,
>Isabelle

And his reply:

>Hi Isabelle,
> 
>Thank you for the detailed explanation. It made things easier to understand.
> 
>We will not need scales to accept double flats or double sharps (i.e. your first suggestion). Could you please record this (and other such decisions) into your design documentation file? Perhaps you can have a link to this email for context. We can then revisit it if necessary and do your second suggestion – however, we won’t for now.
> 
>Have a good weekend!
>Moffat



## The decision was made to not count partial marks in the musical terms tutor

The decision was decided on the 27th of april during a meeting with Moffat, when he said that he did not want the user to receive partial marks for questions in the musical terms tutor.


## Rhythm decision. I decided to give the user the ability to be to set custom swing timing divisions.

The decision was decided to extend the rhythm command, as the functionality to set rhythm based on beat divisions i.e. (0.5, 0.25, 0.25)
was required to be able to be compatible with undo/redo functionality. So the decision to open up a command to be allow the user meant only slight
additions.

## With regards to key signatures, we decided the following:

Assuming the user input is the number of flats or sharps they are looking for (eg, 2#, 7b), we decided to disallow 0# and 0b, instead allowing for a custom command - 0#b.
This finds the scales whose key signatures have no sharps or flats.

This decision was made Friday 20th May, based on the musical understanding of our team.

## Decisions make regarding Keyboard with answers from Moffat

1. When the display note labels setting is on, at the moment we are not displaying labels on the black keys. The reason for this is that they have two possible names (e.g. F# or Gb) and the are much smaller so even fitting one name means the text has to be small. We can add the black key labels if you prefer though.

Answer: Leave it as it is.

2. The acceptance criteria specifies that a current octave label is required, however our note labels include the octave (e.g. C3) so this doesn't seem necessary as a separate thing. Thoughts?

Answer: Leave it as it is.

3. Currently the only way to adjust the displayed keys is with the range slider in the settings panel. This seems not totally intuitive when using the touch screen. I was thinking of adding zoom gestures to scale the keyboard as well. This would take 1-2hrs. Do you think it is a good idea?

Answer: The zoom gesture is a little bit of gold plating at this stage as you have the slider. Keep this as a note that we could look at in the next sprint if we have time.

4. My last question is more vague. We basically said that we would discuss the keyboard input options once we had the keyboard working, but unfortunately the holidays have been busy for our whole team and the keyboard is only done now which doesn't really leave time for a meeting. So would you like to be able to press a key and enter that key as text in the command? If so, would that involve some way of changing mode, from playing mode to input mode? 

Answer: Yes to a toggle button that toggles between “input” and “play” (or suitable words).

## Decision regarding reading from tutor files

It was discovered that the tutor files would have to be manually parsed if we left them as is, so we are going to change the way they are saved.
Moffat is happy for us to do this. For old tutor files he would like us to just have a message that files relate to a previous version and are not compatible.

If we have time and the PO approves, we will create a utility to convert the old files to the new format.

## Locked Badges decision

When displaying locked badges (not yet achieved) it had to be decided how to differentiate between locked and unlocked badges. After consulting the team, the conclusion came to would be to grey out the locked badges and show a lock icon on top.

