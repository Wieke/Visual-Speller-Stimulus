Visual-Speller-Stimulus
=======================

Stimulus presentation for a visual speller, created using the libGDX framework.

So far only contains a proof of concept stimulus presentation in order to test performance.

Definition
==========

Based on [jadref's matrixSpeller](https://github.com/jadref/buffer_bci/tree/master/matrixSpeller).

```
Training:
	Send event stimulus.training with value start
	Show grid in gray 2000 ms

	Sequence repeat 5 times:
		Send event stimulus.sequence with value start
		send event stimulus.targetSymbol with value of target
		Show target symbol in green 2000 ms

		Stimulus repeat 5 times for each column and row:
			Show grid in gray 150 ms
			Send event stimulus.rowFlash with value of flashed row
			Send event stimulus.columnFlash with value of flashed column
			Send event stimulus.tgtFlash with true if row/column contains target
			Flash column/rows white 150 ms

		Send event stimulus.sequence with value end

	send event stimulus.training with value end

Feedback:
	Send event stimulus.feedback with value start
	Show grid in gray 2000 ms

	Sequence repeat X times:
		Send event stimulus.sequence with value start

		Stimulus repeat 5 times for each column and row:
			Show grid in gray 150 ms
			Send event stimulus.rowFlash with value of flashed row
			Send event stimulus.columnFlash with value of flashed column
			Flash column/rows white 150 ms
		
		Show grid grey 950 ms (waiting for the classifier)
		Send event stimulus.sequence with value end
		
		Get new classifier events.
		Correlate classifier events with flash sequence to determine prediction symbol.
		Send event stimulus.prediction with value of prediction symbol.
		Show prediction symbol in red 5000 ms
		
	send event stimulus.feed with value end
```
